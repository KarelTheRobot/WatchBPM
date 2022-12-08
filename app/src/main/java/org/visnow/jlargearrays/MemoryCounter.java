/*
 * JLargeArrays
 * Copyright (C) 2006-2019 University of Warsaw, ICM
 * Copyright (C) 2020 onward visnow.org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 */
package org.visnow.jlargearrays;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility for counting the amount of memory used by large arrays.
 *
 * @author Piotr Wendykier (p.wendykier@icm.edu.pl)
 */
public class MemoryCounter
{

    private static final AtomicLong COUNTER = new AtomicLong(0);

    private MemoryCounter()
    {
    }

    /**
     * Returns the amount of allocated native memory in bytes
     *
     * @return the amount of allocated native memory in bytes
     */
    public static long getCounter()
    {
        return COUNTER.get();
    }

    /**
     * Increases the amount of allocated native memory by specified number of bytes
     *
     * @param x number of bytes
     */
    public static void increaseCounter(long x)
    {
        COUNTER.addAndGet(x);
    }

    /**
     * Decreases the amount of allocated native memory by specified number of bytes
     *
     * @param x number of bytes
     */
    public static void decreaseCounter(long x)
    {
        if (COUNTER.addAndGet(-x) < 0) {
            COUNTER.set(0);
        }
    }
}
