/*
 * Copyright 2012-2023 the original author or authors.
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

package io.spring.initializr.generator.language;

import java.io.StringWriter;

import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.io.SimpleIndentStrategy;
import io.spring.initializr.generator.language.CodeBlock.FormattingOptions;
import org.junit.jupiter.api.Test;

import org.springframework.util.StringUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link CodeBlock}.
 *
 * @author Stephane Nicoll
 */
class CodeBlockTests {

	@Test
	void codeBlockWithPlaceholderAndTooFewArguments() {
		assertThatIllegalArgumentException().isThrownBy(() -> CodeBlock.of("$T.doStuff()"))
			.withMessage("Argument mismatch for '$T.doStuff()', expected at least 1 argument, got 0");
	}

	@Test
	void codeBlockWithPlaceholderAndTooManyArguments() {
		assertThatIllegalArgumentException().isThrownBy(() -> CodeBlock.of("$T.doStuff()", String.class, Integer.class))
			.withMessage("Argument mismatch for '$T.doStuff()', expected 1 argument, got 2");
	}

	@Test
	void codeBlockWithInvalidPlaceholder() {
		assertThatIllegalArgumentException().isThrownBy(() -> CodeBlock.of("$X.doStuff()", 123))
			.withMessage("Unsupported placeholder '$X' for '$X.doStuff()'");
	}

	@Test
	void codeBlockWithTrailingDollarSign() {
		assertThatIllegalArgumentException().isThrownBy(() -> CodeBlock.of("doStuff()$"))
			.withMessage("Should not end with '$': 'doStuff()$'");
	}

	@Test
	void codeBlockWithStringPlaceholder() {
		CodeBlock code = CodeBlock.of("return myUtil.truncate($S)", "value");
		assertThat(writeJava(code)).isEqualTo("return myUtil.truncate(\"value\")");
	}

	@Test
	void codeBlockWithStringPlaceholderAndDoubleQuote() {
		CodeBlock code = CodeBlock.of("return myUtil.truncate($S)", "va\"lue");
		assertThat(writeJava(code)).isEqualTo("return myUtil.truncate(\"va\\\"lue\")");
	}

	@Test
	void codeBlockWithStringPlaceholderAndEscapedSingleQuote() {
		CodeBlock code = CodeBlock.of("return myUtil.truncate($S)", "va\'lue");
		assertThat(writeJava(code)).isEqualTo("return myUtil.truncate(\"va'lue\")");
	}

	@Test
	void codeBlockWithLiteralPlaceholder() {
		CodeBlock code = CodeBlock.of("return $L.truncate(myString)", "myUtil");
		assertThat(writeJava(code)).isEqualTo("return myUtil.truncate(myString)");
	}

	@Test
	void codeBlockWithLiteralPlaceHolderUsingCodeBlock() {
		CodeBlock code = CodeBlock.of("return myUtil.add($L, $L)", CodeBlock.of("1"), CodeBlock.of("2"));
		assertThat(writeJava(code)).isEqualTo("return myUtil.add(1, 2)");
	}

	@Test
	void codeBlockWithLiteralPlaceHolderUsingNestedCodeBlock() {
		CodeBlock code = CodeBlock.of("return myUtil.add($L)",
				CodeBlock.of("$L, $L", CodeBlock.of("1"), CodeBlock.of("2")));
		assertThat(writeJava(code)).isEqualTo("return myUtil.add(1, 2)");
	}

	@Test
	void codeBlockWithDollarSignPlaceholder() {
		CodeBlock code = CodeBlock.of("// $$ allowed, that's $$$L.", 25);
		assertThat(writeJava(code)).isEqualTo("// $ allowed, that's $25.");
	}

	@Test
	void codeBlockWithEndOfStatementPlaceholderInvokeConfiguredFormattingOptions() {
		FormattingOptions options = mock(FormattingOptions.class);
		given(options.statementSeparator()).willReturn(";");
		CodeBlock code = CodeBlock.of("invoke(param)$]");
		assertThat(write(code, options)).isEqualToNormalizingNewlines("""
				invoke(param);
				""");
		verify(options).statementSeparator();
	}

	@Test
	void codeBlockWithTypePlaceholderAndClassAddsImport() {
		CodeBlock code = CodeBlock.of("return $T.truncate(myString)", StringUtils.class);
		assertThat(writeJava(code)).isEqualTo("return StringUtils.truncate(myString)");
		assertThat(code.getImports()).containsExactly(StringUtils.class.getName());
	}

	@Test
	void codeBlockWithTypePlaceholderAndClassNameAddsImport() {
		CodeBlock code = CodeBlock.of("return $T.truncate(myString)", ClassName.of(StringUtils.class));
		assertThat(writeJava(code)).isEqualTo("return StringUtils.truncate(myString)");
		assertThat(code.getImports()).containsExactly(StringUtils.class.getName());
	}

	@Test
	void codeBlockWithTypePlaceholderAndFullyQualifiedClassNameAddsImport() {
		CodeBlock code = CodeBlock.of("return $T.truncate(myString)", "com.example.StringUtils");
		assertThat(writeJava(code)).isEqualTo("return StringUtils.truncate(myString)");
		assertThat(code.getImports()).containsExactly("com.example.StringUtils");
	}

	@Test
	void codeBlockWithTypePlaceholderAndNonResolvableType() {
		assertThatIllegalArgumentException().isThrownBy(() -> CodeBlock.of("return $T.truncate(myString)", true))
			.withMessageContaining("Failed to extract type from 'true'");
	}

	@Test
	void codeBlockDoesNotAddNewLine() {
		CodeBlock code = CodeBlock.of("(123, 456)");
		assertThat(writeJava(code)).isEqualTo("(123, 456)");
		assertThat(code.getImports()).isEmpty();
	}

	@Test
	void codeBlocksCanBeAdded() {
		CodeBlock code = CodeBlock.builder().add("(123").add(CodeBlock.of(", 456)")).build();
		assertThat(writeJava(code)).isEqualTo("(123, 456)");
		assertThat(code.getImports()).isEmpty();
	}

	@Test
	void codeBlockWithSingleStatement() {
		CodeBlock code = CodeBlock.ofStatement("myInstance.sayHello(123)");
		assertThat(writeJava(code)).isEqualToNormalizingNewlines("""
				myInstance.sayHello(123);
				""");
		assertThat(code.getImports()).isEmpty();
	}

	@Test
	void codeBlockWithMultipleStatements() {
		CodeBlock code = CodeBlock.builder()
			.addStatement("myInstance.sayHello(123)")
			.addStatement(CodeBlock.of("myInstance.sayHello(456)"))
			.build();
		assertThat(writeJava(code)).isEqualToNormalizingNewlines("""
				myInstance.sayHello(123);
				myInstance.sayHello(456);
				""");
		assertThat(code.getImports()).isEmpty();
	}

	@Test
	void codeBlockWithParameterCodeBlockAddsImports() {
		CodeBlock code = CodeBlock.of("$L", CodeBlock.of("$T.truncate(myString)", ClassName.of(StringUtils.class)));
		assertThat(code.getImports()).containsExactly(StringUtils.class.getName());
	}

	private String writeJava(CodeBlock code) {
		return write(code, CodeBlock.JAVA_FORMATTING_OPTIONS);
	}

	private String write(CodeBlock code, FormattingOptions options) {
		StringWriter out = new StringWriter();
		IndentingWriter writer = new IndentingWriter(out, new SimpleIndentStrategy("\t"));
		code.write(writer, options);
		return out.toString();
	}

}
