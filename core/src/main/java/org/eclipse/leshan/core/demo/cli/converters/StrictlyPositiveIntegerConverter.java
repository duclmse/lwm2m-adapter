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

import picocli.CommandLine.ITypeConverter;

public class StrictlyPositiveIntegerConverter implements ITypeConverter<Integer> {

    @Override
    public Integer convert(String value) {
        Integer res = Integer.parseInt(value);

        if (res <= 0)
            throw new IllegalArgumentException(String.format("%s is not a strictly positive integer", value));

        return res;
    }
}
