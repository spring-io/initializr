/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.web.project;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stephane Nicoll
 */
class HomePage {

	@FindBy(id = "form")
	private WebElement form;

	private final WebDriver driver;

	HomePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public Object value(String id) {
		return getInputValue(this.form.findElement(By.id(id)));
	}

	private Object getInputValue(WebElement input) {
		Object value = null;
		String type = input.getAttribute("type");
		if ("select".equals(input.getTagName())) {
			Select select = new Select(input);
			if (select.isMultiple()) {
				value = select.getAllSelectedOptions().stream().map(this::getValue)
						.collect(Collectors.toList());
			}
			else {
				value = getValue(select.getFirstSelectedOption());
			}
		}
		else if (Arrays.asList("checkbox", "radio").contains(type)) {
			if (input.isSelected()) {
				value = getValue(input);
			}
			else {
				if (Objects.equals(type, "checkbox")) {
					value = false;
				}
			}
		}
		else {
			value = getValue(input);
		}
		return value;
	}

	private String getValue(WebElement input) {
		return input.getAttribute("value");
	}

	public WebElement dependency(String value) {
		for (WebElement element : this.form.findElements(By.name("style"))) {
			if (value.equals(element.getAttribute("value"))) {
				return element;
			}
		}
		throw new AssertionError("Dependency not found: " + value);
	}

	public void advanced() {
		this.form.findElement(By.cssSelector(".tofullversion"))
				.findElement(By.tagName("a")).click();
	}

	public void simple() {
		this.form.findElement(By.cssSelector(".tosimpleversion")).click();
	}

	public void artifactId(String text) {
		this.form.findElement(By.id("artifactId")).clear();
		this.form.findElement(By.id("artifactId")).sendKeys(text);
	}

	public void autocomplete(String text) {
		this.form.findElement(By.id("autocomplete")).sendKeys(text);
	}

	public void bootVersion(String text) {
		this.form.findElement(By.id("bootVersion")).sendKeys(text);
		this.form.click();
	}

	public void description(String text) {
		this.form.findElement(By.id("description")).clear();
		this.form.findElement(By.id("description")).sendKeys(text);
	}

	public void groupId(String text) {
		this.form.findElement(By.id("groupId")).clear();
		this.form.findElement(By.id("groupId")).sendKeys(text);
	}

	public void language(String text) {
		this.form.findElement(By.id("language")).sendKeys(text);
	}

	public void name(String text) {
		this.form.findElement(By.id("name")).clear();
		this.form.findElement(By.id("name")).sendKeys(text);
	}

	public void packaging(String text) {
		this.form.findElement(By.id("packaging")).sendKeys(text);
	}

	public void packageName(String text) {
		this.form.findElement(By.id("packageName")).clear();
		this.form.findElement(By.id("packageName")).sendKeys(text);
	}

	public void type(String text) {
		this.form.findElement(By.id("type")).sendKeys(text);
	}

	public HomePage submit() {
		String url = this.driver.getCurrentUrl();
		this.form.findElement(By.name("generate-project")).click();
		assertThat(this.driver.getCurrentUrl()).isEqualTo(url);
		return this;
	}

}
