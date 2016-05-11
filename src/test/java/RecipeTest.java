import org.sql2o.*;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;


public class RecipeTest {

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Test
  public void Recipe_instantiatesCorrectly_true() {
    Recipe myRecipe = new Recipe("Tacos", "meat", "cook", 5);
    assertEquals(true, myRecipe instanceof Recipe);
  }

  @Test
  public void getTitle_recipeInstantiatesWithTitle_String() {
    Recipe myRecipe = new Recipe("Tacos", "meat", "cook", 5);
    assertEquals("Tacos", myRecipe.getTitle());
  }

  @Test
  public void all_emptyAtFirst_0() {
    assertEquals(0, Recipe.all().size());
  }

  @Test
  public void equals_returnsTrueIfTitlesAretheSame_true() {
    Recipe firstRecipe = new Recipe("Tacos", "meat", "cook", 5);
    Recipe secondRecipe = new Recipe("Tacos", "meat", "cook", 5);
    assertTrue(firstRecipe.equals(secondRecipe));
  }

  @Test
  public void save_savesObjectIntoDatabase_true() {
    Recipe myRecipe = new Recipe("Tacos", "meat", "cook", 5);
    myRecipe.save();
    assertTrue(Recipe.all().get(0).equals(myRecipe));
  }

  @Test
  public void save_assignsIdToObject_int() {
    Recipe myRecipe = new Recipe("Tacos", "meat", "cook", 5);
    myRecipe.save();
    Recipe savedRecipe = Recipe.all().get(0);
    assertEquals(myRecipe.getId(), savedRecipe.getId());
  }

  @Test
  public void find_findsRecipeInDatabase_true() {
    Recipe myRecipe = new Recipe("Tacos", "meat", "cook", 5);
    myRecipe.save();
    Recipe savedRecipe = Recipe.find(myRecipe.getId());
    assertTrue(myRecipe.equals(savedRecipe));
  }

  @Test
  public void update_updatesRecipeTitle_true() {
    Recipe myRecipe = new Recipe("Tacos", "meat", "cook", 5);
    myRecipe.save();
    myRecipe.update("Burgers");
    assertEquals("Burgers", Recipe.find(myRecipe.getId()).getTitle());
  }

  @Test
  public void delete_deletesRecipe_true() {
    Recipe myRecipe = new Recipe("Tacos", "meat", "cook", 5);
    myRecipe.save();
    int myRecipeId = myRecipe.getId();
    myRecipe.delete();
    assertEquals(null, Recipe.find(myRecipeId));
  }

  @Test
  public void addCategory_addsCategoryToRecipe() {
    Category myCategory = new Category("Mexican");
    myCategory.save();
    Recipe myRecipe = new Recipe("Tacos", "meat", "cook", 5);
    myRecipe.save();
    myRecipe.addCategory(myCategory);
    Category savedCategory = myRecipe.getCategories().get(0);
    assertTrue(myCategory.equals(savedCategory));
  }

  @Test
  public void getCategories_returnsAllCategories_List() {
    Category myCategory = new Category("Mexican");
    myCategory.save();
    Recipe myRecipe = new Recipe("Tacos", "meat", "cook", 5);
    myRecipe.save();
    myRecipe.addCategory(myCategory);
    List savedCategories = myRecipe.getCategories();
    assertEquals(1, savedCategories.size());
  }

  @Test
  public void delete_deletesAllRecipesAndCategoriesAssociations() {
    Category myCategory = new Category("Mexican");
    myCategory.save();
    Recipe myRecipe = new Recipe("Tacos", "meat", "cook", 5);
    myRecipe.save();
    myRecipe.addCategory(myCategory);
    myRecipe.delete();
    assertEquals(0, myCategory.getRecipes().size());
  }


}
