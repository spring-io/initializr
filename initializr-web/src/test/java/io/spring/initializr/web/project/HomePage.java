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

package io.spring.initializr.web.project;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stephane Nicoll
 * @author Brian Clozel
 */
class HomePage {

	@FindBy(tagName = "form")
	private WebElement form;

	private final WebDriver driver;

	private final JavascriptExecutor js;

	HomePage(WebDriver driver) {
		this.driver = driver;
		this.js = ((JavascriptExecutor) driver);
		PageFactory.initElements(driver, this);
	}

	public Object value(String id) {
		return this.form.findElement(By.name(id)).getAttribute("value");
	}

	public boolean hasSelectedDependency(String value) {
		for (WebElement element : this.form.findElements(By.name("style"))) {
			if (value.equals(element.getAttribute("value"))) {
				return true;
			}
		}
		return false;
	}

	public void clickOnMoreOptions() {
		this.js.executeScript("window.scrollTo(0, -document.body.scrollHeight);");
		WebDriverWait wait = new WebDriverWait(this.driver, 1);
		WebElement moreOptionsButton = this.form
				.findElement(By.cssSelector("#more-options button"));
		wait.until(ExpectedConditions.visibilityOf(moreOptionsButton));
		moreOptionsButton.click();
	}

	public void clickOnFewerOptions() {
		this.js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
		WebDriverWait wait = new WebDriverWait(this.driver, 1);
		WebElement fewerOptionsButton = this.form
				.findElement(By.cssSelector("#fewer-options button"));
		wait.until(ExpectedConditions.visibilityOf(fewerOptionsButton));
		fewerOptionsButton.click();
	}

	public void artifactId(String text) {
		this.form.findElement(By.name("artifactId")).clear();
		this.form.findElement(By.name("artifactId")).sendKeys(text);
	}

	public SearchFieldAutoCompletion typeInSearchField(String text) {
		this.form.findElement(By.id("inputSearch")).sendKeys(text);
		return new SearchFieldAutoCompletion(this.driver);
	}

	public void clearSearchField() {
		this.form.findElement(By.id("inputSearch")).clear();
	}

	public List<String> getSearchResults() {
		return this.form.findElements(By.cssSelector(("#list-to-add .item"))).stream()
				.filter((element) -> !element.getAttribute("class").contains("invalid"))
				.map((element) -> element.getAttribute("data-id"))
				.collect(Collectors.toList());
	}

	public List<String> getInvalidSearchResults() {
		return this.form.findElements(By.cssSelector(("#list-to-add .item.invalid")))
				.stream().map((element) -> element.getAttribute("data-id"))
				.collect(Collectors.toList());
	}

	public void bootVersion(String text) {
		this.form.findElement(By.cssSelector("a[data-value='" + text + "']")).click();
	}

	public void description(String text) {
		this.form.findElement(By.name("description")).clear();
		this.form.findElement(By.name("description")).sendKeys(text);
	}

	public void groupId(String text) {
		this.form.findElement(By.name("groupId")).clear();
		this.form.findElement(By.name("groupId")).sendKeys(text);
	}

	public void language(String text) {
		this.form.findElement(By.cssSelector("a[data-value='" + text + "']")).click();
	}

	public void name(String text) {
		this.form.findElement(By.name("name")).clear();
		this.form.findElement(By.name("name")).sendKeys(text);
	}

	public void packaging(String text) {
		this.form.findElement(By.cssSelector("a[data-value='" + text + "']")).click();
	}

	public void packageName(String text) {
		this.form.findElement(By.name("packageName")).clear();
		this.form.findElement(By.name("packageName")).sendKeys(text);
	}

	public void type(String text) {
		this.form.findElement(By.cssSelector("a[data-value='" + text + "']")).click();
	}

	public HomePage submit() {
		String url = this.driver.getCurrentUrl();
		this.form.findElement(By.cssSelector(".submit button")).click();
		assertThat(this.driver.getCurrentUrl()).isEqualTo(url);
		return this;
	}

	class SearchFieldAutoCompletion {

		private final Action action;

		SearchFieldAutoCompletion(WebDriver driver) {
			Actions actions = new Actions(driver);
			this.action = actions.sendKeys(Keys.ENTER).build();
		}

		public void andHitEnter() {
			this.action.perform();
		}

	}

}
