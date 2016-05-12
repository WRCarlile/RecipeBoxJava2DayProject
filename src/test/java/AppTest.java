import org.sql2o.*;
import org.junit.*;
import org.fluentlenium.adapter.FluentTest;
import org.junit.ClassRule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.*;
import static org.junit.Assert.*;

public class AppTest extends FluentTest {
  public WebDriver webDriver = new HtmlUnitDriver();

  @Override
  public WebDriver getDefaultDriver() {
    return webDriver;
  }

  @ClassRule
  public static ServerRule server = new ServerRule();

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Test
  public void rootTest() {
    goTo("http://localhost:4567/");
    assertThat(pageSource()).contains("Recipe Box");
  }

  @Test
  public void categoryIsCreatedTest() {
    goTo("http://localhost:4567/");
    click("a", withText("Categories"));
    fill("#name").with("Mexican");
    submit(".btn");
    assertThat(pageSource()).contains("Mexican");
  }

  @Test
  public void recipeIsCreatedTest() {
    goTo("http://localhost:4567/");
    click("a", withText("Recipes"));
    fill("#title").with("Tacos");
    fill("#ingredients").with("Meat");
    fill("#instructions").with("Cook");
    fill("#rating").with("5");
    submit("#addRecipe");
    assertThat(pageSource()).contains("Tacos");
  }

  @Test
  public void categoryShowPageDisplaysName() {
    Category testCategory = new Category("Mexican");
    testCategory.save();
    String url = String.format("http://localhost:4567/categories/%d", testCategory.getId());
    goTo(url);
    assertThat(pageSource()).contains("Mexican");
  }

  @Test
  public void recipeShowPageDisplaysTitle() {
    Recipe testRecipe = new Recipe("Tacos", "meat", "cook", 5);
    testRecipe.save();
    String url = String.format("http://localhost:4567/recipes/%d", testRecipe.getId());
    goTo(url);
    assertThat(pageSource()).contains("Tacos");
  }
  //
  @Test
  public void recipeIsAddedToCategory() {
    Category testCategory = new Category("Mexican");
    testCategory.save();
    Recipe testRecipe = new Recipe("Tacos", "meat", "cook", 5);
    testRecipe.save();
    String url = String.format("http://localhost:4567/categories/%d", testCategory.getId());
    goTo(url);
    fillSelect("#recipe_id").withText("Tacos");
    submit("#selectRecipe");
    assertThat(pageSource()).contains("<li>");
    assertThat(pageSource()).contains("Tacos");
  }

  @Test
  public void categoryIsAddedToRecipe() {
    Category testCategory = new Category("Mexican");
    testCategory.save();
    Recipe testRecipe = new Recipe("Tacos", "meat", "cook", 5);
    testRecipe.save();
    String url = String.format("http://localhost:4567/recipes/%d", testRecipe.getId());
    goTo(url);
    fillSelect("#category_id").withText("Mexican");
    submit("#addCategory");
    assertThat(pageSource()).contains("<li>");
    assertThat(pageSource()).contains("Mexican");
  }
  @Test
  public void recipeIsUpdated() {
    Recipe testRecipe = new Recipe("Tacos", "meat", "cook", 5);
    testRecipe.save();
    String url = String.format("http://localhost:4567/recipes/%d", testRecipe.getId());
    goTo(url);
    click("a", withText("Edit this recipe"));
    fill("#title").with("Burgers");
    fill("#ingredients").with("Beef");
    fill("#instructions").with("Grill");
    fill("#rating").with("5");
    submit(".btn");
    goTo(url);
    assertThat(pageSource()).contains("Burgers");
  }

  @Test
  public void recipeIsDeleted() {
    Recipe testRecipe = new Recipe("Tacos", "meat", "cook", 5);
    testRecipe.save();
    String url = String.format("http://localhost:4567/recipes/%d", testRecipe.getId());
    goTo(url);
    submit("#delete");
    goTo(url);
    assertThat(pageSource()).contains("$recipe.getTitle()");
  }

}
