/*******************************************************************************
 * Copyright (c) 2021 Sierra Wireless and others.
 * <p>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * <p>
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * <p>
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.leshan.core.demo.cli.converters;

import org.eclipse.leshan.core.node.LwM2mPath;

import picocli.CommandLine.ITypeConverter;

public class StringLwM2mPathConverter implements ITypeConverter<String> {

    @Override
    public String convert(String value) {
        // create a LwM2mPath to force validation.
        new LwM2mPath(value);
        return value;
    }
}