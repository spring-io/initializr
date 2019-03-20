/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator.test.assertj;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AssertProvider;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.StringAssert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * AssertJ {@link AssertProvider} for {@link Node} assertions.
 *
 * @author Andy Wilkinson
 */
public class NodeAssert extends AbstractAssert<NodeAssert, Node>
		implements AssertProvider<NodeAssert> {

	private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory
			.newInstance();

	private final XPathFactory xpathFactory = XPathFactory.newInstance();

	private final XPath xpath = this.xpathFactory.newXPath();

	public NodeAssert(Path xmlFile) {
		this(read(xmlFile));
	}

	public NodeAssert(String xmlContent) {
		this(read(xmlContent));
	}

	public NodeAssert(Node actual) {
		super(actual, NodeAssert.class);
	}

	private static Document read(Path xmlFile) {
		try {
			return FACTORY.newDocumentBuilder().parse(xmlFile.toFile());
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private static Document read(String xmlContent) {
		try {
			return FACTORY.newDocumentBuilder().parse(new ByteArrayInputStream(
					xmlContent.getBytes(StandardCharsets.UTF_8)));
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public NodeAssert nodeAtPath(String xpath) {
		try {
			return new NodeAssert(
					(Node) this.xpath.evaluate(xpath, this.actual, XPathConstants.NODE));
		}
		catch (XPathExpressionException ex) {
			throw new RuntimeException(ex);
		}
	}

	public ListAssert<Node> nodesAtPath(String xpath) {
		try {
			NodeList nodeList = (NodeList) this.xpath.evaluate(xpath, this.actual,
					XPathConstants.NODESET);
			return new ListAssert<>(toList(nodeList));
		}
		catch (XPathExpressionException ex) {
			throw new RuntimeException(ex);
		}
	}

	public StringAssert textAtPath(String xpath) {
		try {
			return new StringAssert((String) this.xpath.evaluate(xpath + "/text()",
					this.actual, XPathConstants.STRING));
		}
		catch (XPathExpressionException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public NodeAssert assertThat() {
		return this;
	}

	private static List<Node> toList(NodeList nodeList) {
		List<Node> nodes = new ArrayList<>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			nodes.add(nodeList.item(i));
		}
		return nodes;
	}

}
