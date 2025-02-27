/*
*
* Copyright (c) 2025 dotCMS LLC
* Use of this software is governed by the Business Source License included 
* in the LICENSE file found at in the root directory of software.
* SPDX-License-Identifier: BUSL-1.1
*
*/

package com.dotcms.enterprise.license.bouncycastle.crypto.engines;

import com.dotcms.enterprise.license.bouncycastle.crypto.BlockCipher;
import com.dotcms.enterprise.license.bouncycastle.crypto.CipherParameters;
import com.dotcms.enterprise.license.bouncycastle.crypto.DataLengthException;
import com.dotcms.enterprise.license.bouncycastle.crypto.params.KeyParameter;

/**
 * An TEA engine.
 */
public class TEAEngine
    implements BlockCipher
{
    private static final int rounds     = 32,
                             block_size = 8,
//                             key_size   = 16,
                             delta      = 0x9E3779B9,
                             d_sum      = 0xC6EF3720; // sum on decrypt
    /*
     * the expanded key array of 4 subkeys
     */
    private int _a, _b, _c, _d;
    private boolean _initialised;
    private boolean _forEncryption;

    /**
     * Create an instance of the TEA encryption algorithm
     * and set some defaults
     */
    public TEAEngine()
    {
        _initialised = false;
    }

    public String getAlgorithmName()
    {
        return "TEA";
    }

    public int getBlockSize()
    {
        return block_size;
    }

    /**
     * initialise
     *
     * @param forEncryption whether or not we are for encryption.
     * @param params the parameters required to set up the cipher.
     * @exception IllegalArgumentException if the params argument is
     * inappropriate.
     */
    public void init(
        boolean             forEncryption,
        CipherParameters    params)
    {
        if (!(params instanceof KeyParameter))
        {
            throw new IllegalArgumentException("invalid parameter passed to TEA init - " + params.getClass().getName());
        }

        _forEncryption = forEncryption;
        _initialised = true;

        KeyParameter       p = (KeyParameter)params;

        setKey(p.getKey());
    }

    public int processBlock(
        byte[]  in,
        int     inOff,
        byte[]  out,
        int     outOff)
    {
        if (!_initialised)
        {
            throw new IllegalStateException(getAlgorithmName()+" not initialised");
        }
        
        if ((inOff + block_size) > in.length)
        {
            throw new DataLengthException("input buffer too short");
        }
        
        if ((outOff + block_size) > out.length)
        {
            throw new DataLengthException("output buffer too short");
        }
        
        return (_forEncryption) ? encryptBlock(in, inOff, out, outOff)
                                    : decryptBlock(in, inOff, out, outOff);
    }

    public void reset()
    {
    }

    /**
     * Re-key the cipher.
     * <p>
     * @param  key  the key to be used
     */
    private void setKey(
        byte[]      key)
    {
        _a = bytesToInt(key, 0);
        _b = bytesToInt(key, 4);
        _c = bytesToInt(key, 8);
        _d = bytesToInt(key, 12);
    }

    private int encryptBlock(
        byte[]  in,
        int     inOff,
        byte[]  out,
        int     outOff)
    {
        // Pack bytes into integers
        int v0 = bytesToInt(in, inOff);
        int v1 = bytesToInt(in, inOff + 4);
        
        int sum = 0;
        
        for (int i = 0; i != rounds; i++)
        {
            sum += delta;
            v0  += ((v1 << 4) + _a) ^ (v1 + sum) ^ ((v1 >>> 5) + _b);
            v1  += ((v0 << 4) + _c) ^ (v0 + sum) ^ ((v0 >>> 5) + _d);
        }

        unpackInt(v0, out, outOff);
        unpackInt(v1, out, outOff + 4);
        
        return block_size;
    }

    private int decryptBlock(
        byte[]  in,
        int     inOff,
        byte[]  out,
        int     outOff)
    {
        // Pack bytes into integers
        int v0 = bytesToInt(in, inOff);
        int v1 = bytesToInt(in, inOff + 4);
        
        int sum = d_sum;
        
        for (int i = 0; i != rounds; i++)
        {
            v1  -= ((v0 << 4) + _c) ^ (v0 + sum) ^ ((v0 >>> 5) + _d);
            v0  -= ((v1 << 4) + _a) ^ (v1 + sum) ^ ((v1 >>> 5) + _b);
            sum -= delta;
        }
        
        unpackInt(v0, out, outOff);
        unpackInt(v1, out, outOff + 4);
        
        return block_size;
    }

    private int bytesToInt(byte[] in, int inOff)
    {
        return ((in[inOff++]) << 24) |
                 ((in[inOff++] & 255) << 16) |
                 ((in[inOff++] & 255) <<  8) |
                 ((in[inOff] & 255));
    }

    private void unpackInt(int v, byte[] out, int outOff)
    {
        out[outOff++] = (byte)(v >>> 24);
        out[outOff++] = (byte)(v >>> 16);
        out[outOff++] = (byte)(v >>>  8);
        out[outOff  ] = (byte)v;
    }
}
