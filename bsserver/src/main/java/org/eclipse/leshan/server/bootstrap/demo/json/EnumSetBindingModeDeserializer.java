/*******************************************************************************
 * Copyright (c) 2021 Orange.
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
 *     Orange - keep one JSON dependency
 *******************************************************************************/
package org.eclipse.leshan.server.bootstrap.demo.json;

import java.io.IOException;
import java.util.EnumSet;

import org.eclipse.leshan.core.request.BindingMode;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;

public class EnumSetBindingModeDeserializer extends JsonDeserializer<EnumSet<BindingMode>> {

    @Override
    public EnumSet<BindingMode> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        TreeNode treeNode = p.getCodec().readTree(p);

        return treeNode instanceof TextNode ? BindingMode.parse(((TextNode) treeNode).asText()) : null;
    }
}
