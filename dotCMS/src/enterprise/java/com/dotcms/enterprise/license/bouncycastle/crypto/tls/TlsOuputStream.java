/*
*
* Copyright (c) 2025 dotCMS LLC
* Use of this software is governed by the Business Source License included
* in the LICENSE file found at in the root directory of software.
* SPDX-License-Identifier: BUSL-1.1
*
*/

package com.dotcms.enterprise.license.bouncycastle.crypto.tls;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An OutputStream for an TLS connection.
 */
public class TlsOuputStream extends OutputStream
{
    private byte[] buf = new byte[1];
    private TlsProtocolHandler handler;

    TlsOuputStream(TlsProtocolHandler handler)
    {
        this.handler = handler;
    }

    public void write(byte buf[], int offset, int len) throws IOException
    {
        this.handler.writeData(buf, offset, len);
    }

    public void write(int arg0) throws IOException
    {
        buf[0] = (byte)arg0;
        this.write(buf, 0, 1);
    }

    /** @deprecated Use 'close' instead */
    public void cose() throws IOException
    {
        handler.close();
    }

    public void close() throws IOException
    {
        handler.close();
    }

    public void flush() throws IOException
    {
        handler.flush();
    }
}
