/*
*
* Copyright (c) 2025 dotCMS LLC
* Use of this software is governed by the Business Source License included
* in the LICENSE file found at in the root directory of software.
* SPDX-License-Identifier: BUSL-1.1
*
*/

package com.dotcms.enterprise.license.bouncycastle.crypto.params;

import com.dotcms.enterprise.license.bouncycastle.crypto.CipherParameters;

import java.math.BigInteger;

public class GOST3410Parameters
   implements CipherParameters
{
    private BigInteger              p;
    private BigInteger              q;
    private BigInteger              a;
    private GOST3410ValidationParameters validation;

    public GOST3410Parameters(
        BigInteger  p,
        BigInteger  q,
        BigInteger  a)
    {
        this.p = p;
        this.q = q;
        this.a = a;
    }

    public GOST3410Parameters(
        BigInteger              p,
        BigInteger              q,
        BigInteger              a,
        GOST3410ValidationParameters params)
    {
        this.a = a;
        this.p = p;
        this.q = q;
        this.validation = params;
    }

    public BigInteger getP()
    {
        return p;
    }

    public BigInteger getQ()
    {
        return q;
    }

    public BigInteger getA()
    {
        return a;
    }

    public GOST3410ValidationParameters getValidationParameters()
    {
        return validation;
    }

    public int hashCode()
    {
        return p.hashCode() ^ q.hashCode() ^ a.hashCode();
    }

    public boolean equals(
        Object  obj)
    {
        if (!(obj instanceof GOST3410Parameters))
        {
            return false;
        }

        GOST3410Parameters    pm = (GOST3410Parameters)obj;

        return (pm.getP().equals(p) && pm.getQ().equals(q) && pm.getA().equals(a));
    }
}
