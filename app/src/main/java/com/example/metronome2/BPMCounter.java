package com.example.metronome2;
import org.apache.commons.math3.*;
import org.jtransforms.*;
import org.jtransforms.fft.FloatFFT_1D;
import org.visnow.jlargearrays.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/*

 */

public class BPMCounter {
    /*
        Example memory layout given
            recordsToKeep = 1024
            updateFrequency = 32
            HISTORY = 32
            **updateFrequency is required to divide into recordsToKeep evenly

        data[] is initialized so that we have HISTORY windows of recordsToKeep values
         In this case, that's 32 windows of 1024 values for 32768 total.

        data = [0, 1, 2, ..., 1021, 1022, 1023 |||| 1024, 1025, 1026, ... , 2045, 2046, 2047 |||| ...]
                        one window                          second window

        Whenever a new data point comes in, we store it in one of these 32768 cells. currPos
         stores the current write index.

        data = [0, 1, 2, ..., 1021, 1022, 1023 |||| 1024, 1025, 1026, ... , 2045, 2046, 2047 |||| ...]
                ^
             currPos

        Each index stores a DataPoint object, which just indicates the x- y- z- accelerometer readings
         as well as a timestamp for when that measurement was taken.

        dataStart is a bit of a weird variable. It's intended to be the boundary between "relevant"
         and "irrelevant" data. Any data before dataStart is effectively forgotten, and will never
         be used in another FFT calculation. dataStart is updated every time we run an FFT (which we
         do every updateFrequency times). dataStart's allowable values are (0, 1, 2, ... , X-1, X)
         ... where X = recordsToKeep/updateFrequency

         Basically, every time we collect 32 more data points, we shift the window, throw out the
          last 32, and increment dataStart by 1. In this sense, dataStart is basically "which
          window of 32 is the first one we read from?" This lets us use data once it's read in, but
          keep using it over the next (recordsToKeep / updateFrequency) FFT calculations as well.

         Once we get to the end of the array, we take the last 1024 items, move them to the first
          1024, and then boom! we can keep recording data starting from 1025. We don't even need to
          zero out the array since we'll just overwrite stuff and Java has a built in garbage
          collector (though trust me, I did try to free() all of this stuff before I remembered that)
          This mass transfer is achieved in the clearHistory() function and occurs very rarely.

         Why is the data stored like this? I wanted something that:
          - Took up fixed memory on the watch (no infinitely growing ArrayList)
          - Did not consistently overwrite the entire array (e.g. delete the first item, add one
                to the end, shift everything over)
     */
    private int recordsToKeep;
    private int updateFrequency;
    private static final int HISTORY = 32;
    private int currPos = 0;
    private int dataStart = 0;

    private static final int MIN_BPM = 40;
    private static final int MAX_BPM = 200;

    private DataPoint[] data;
    private float[] xF;

    private class DataPoint {
        public float x;
        public float y;
        public float z;
        public long timestamp;
        public DataPoint(float x, float y, float z, long timestamp) {
            this.x = x;
            this.y = y;
            this.z = z;
            if (timestamp != -1) {
                this.timestamp = timestamp;
            } else {
                this.timestamp = System.currentTimeMillis();
            }
        }
    }

    public BPMCounter(int recordsToKeep, int updateFrequency) {
        this.recordsToKeep = recordsToKeep;
        this.updateFrequency = updateFrequency;

        /*
            Initialize a buffer of 0's at the start. These will get slowly phased out as real data
            comes in, but are a nice way to smooth out the startup process
            Initialize the write head after the 0's.
         */
        data = new DataPoint[recordsToKeep * HISTORY];
        for (int i = 0; i < recordsToKeep; i++) {
            data[i] = new DataPoint(0, 0, 0, -1);
        }
        // This starts at 1 for the same reason as in record, see below.
        dataStart = 1;
        currPos = recordsToKeep;
    }

    /*
        Generates the X-axis frequencies based on the time to measure one datapoint (positive only)
        https://docs.scipy.org/doc/scipy/reference/generated/scipy.fftpack.fftfreq.html
        Tries to replicate this ^
     */
    private void generateXF(float time_for_one_datapoint) {
        xF = new float[recordsToKeep];
        for (int i = 0; i < recordsToKeep / 2; i++) {
            xF[i] = ((float)i / recordsToKeep) / time_for_one_datapoint;
        }
    }

    /*
        Records a data point. If updateFrequency data points have been collected, run an fft and return the result
     */
    public float record(float x, float y, float z) {
        data[currPos] = new DataPoint(x, y, z, -1);
        currPos += 1;

        // If we've reached a multiple of update frequency, we should run an FFT and send the result back
        if (currPos % updateFrequency == 0) {
            // If we're somehow all the way at the end of the array, dump the old data
            if (currPos == data.length) {
                clearHistory();
            }
            System.out.println("Running fft -- dataStart " + dataStart + "counter:" + currPos);
            float[] resX = run_fft("x");
            float[] resY = run_fft("y");
            float[] resZ = run_fft("z");
            float result = -1;
            if (resX[1] > resY[1] && resX[1] > resZ[1]) {
                System.out.println("Going with X (" + resX[1] + ", " + resY[1] + ", " + resZ[1] + ")");
                result = resX[0];
            } else if (resY[1] > resZ[1]){
                System.out.println("Going with Y (" + resX[1] + ", " + resY[1] + ", " + resZ[1] + ")");
                result = resY[0];
            } else {
                System.out.println("Going with Z (" + resX[1] + ", " + resY[1] + ", " + resZ[1] + ")");
                result = resZ[0];
            }

            /*
                dataStart will never actually represent a full recordsToKeep (there will always be
                trailing 0s).
                Only when an FFT is about to happen is the buffer full, after which dataStart is
                immediately incremented for the next round. Since clearHistory() copies over a full
                buffer's worth of data, we then increment it to 1 for the 1..(1+width) step
             */
            dataStart += 1;
            return result;
        }
        return -1;
    }

    /*
        Moves everything from the end of the array back to the start of the array. Lets the app run
        for an arbitrary amount of time without using up increasing amounts of memory
     */
    private void clearHistory() {
        DataPoint[] d2 = new DataPoint[recordsToKeep * HISTORY];
        if (recordsToKeep >= 0) {
            System.arraycopy(data, recordsToKeep * (HISTORY - 1), d2, 0, recordsToKeep);
        }
        data = d2;
        dataStart = 0;
        currPos = recordsToKeep;
    }

    /*
        Runs an fft on the "x", "y", or "z" dimension.

        Returns a tuple (technically a two-item array) with [frequency, weight]

        Higher weight means there was a bigger peak at that point and it's more confident that
         the frequency was correct. Frequency is measured in beats per second, the Main Activity
         does all the scaling/rounding/etc.
     */
    public float[] run_fft(String dimension) {
        float[] fft = new float[recordsToKeep];
        for (int i = 0; i < recordsToKeep; i++) {
            if (data[dataStart * updateFrequency + i] == null) {
                // This should never happen
                return new float[]{};
            } else {
                switch (dimension) {
                    case "x":
                        fft[i] = data[dataStart * updateFrequency + i].x;
                        break;
                    case "y":
                        fft[i] = data[dataStart * updateFrequency + i].y;
                        break;
                    case "z":
                        fft[i] = data[dataStart * updateFrequency + i].z;
                        break;
                }
            }
        }
        float time_for_one_datapoint =
                (float)(data[dataStart * updateFrequency + recordsToKeep - 1].timestamp - data[dataStart * updateFrequency].timestamp) / (recordsToKeep-1);


        // milliseconds to seconds
        time_for_one_datapoint /= 1000;

        generateXF(time_for_one_datapoint);
        System.out.println("xF: " + Arrays.toString(xF));

        float min_bps = MIN_BPM / 60.0f;
        float max_bps = MAX_BPM / 60.0f;

        int min_index = -1;
        int max_index = -1;

        /*
            We're only interested in results that correspond to reasonable BPM values
         */
        for (int i = 0; i < xF.length; i++) {
            if (min_index == -1 && xF[i] > min_bps) {
                min_index = i;
            }
            if (max_index == -1 && xF[i] > max_bps) {
                max_index = i;
            }
        }

        FloatFFT_1D fftDo = new FloatFFT_1D(recordsToKeep);
        fftDo.realForward(fft);

        /*
            Combine the real and imaginary parts into one magnitude
         */
        float[] realFFT = new float[recordsToKeep / 2];
        for (int i = 0; i < recordsToKeep/2; i++) {
            realFFT[i] = (float)Math.sqrt(fft[2*i]*fft[2*i] + fft[2*i+1]*fft[2*i+1]);
        }

        int temp_index = min_index;
        int best_index = min_index;
        int second_best_index = min_index;

        for (int i = min_index; i < max_index; i++) {
            if (Math.abs(realFFT[i]) > Math.abs(realFFT[temp_index])) {
                temp_index = i;
            }
        }
        best_index = temp_index;
        temp_index = min_index;
        for (int i = min_index; i < max_index; i++) {
            if (Math.abs(realFFT[i]) > Math.abs(realFFT[temp_index]) && i != best_index) {
                temp_index = i;
            }
        }
        second_best_index = temp_index;

        float highest_freq = xF[best_index];
        float second_highest_freq = xF[second_best_index];
        float weight_1st = Math.abs(realFFT[best_index]);
        float weight_2nd = Math.abs(realFFT[second_best_index]);
        System.out.println("Highest frequency: " + highest_freq + " with score " + weight_1st);
        System.out.println("2nd highest frequency: " + second_highest_freq + " with score " + weight_2nd);

        /*
            If the top two frequencies are adjacent, this means the actual peak is probably somewhere in the middle

            Perform a weighted average
         */
        if (Math.abs(second_best_index - best_index) == 1) {
            float res = (highest_freq * weight_1st + second_highest_freq * weight_2nd)/(weight_1st + weight_2nd);
            System.out.println("1 apart, averaging -- result: " + res);
            return new float[]{res, weight_1st};
        }
        return new float[]{highest_freq, weight_1st};
    }
}
