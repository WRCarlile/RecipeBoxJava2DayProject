import java.util.List;
import org.sql2o.*;
import java.util.ArrayList;

public class Recipe {
  private int id;
  private String title;
  private String ingredients;
  private String instructions;
  private int rating;


  public Recipe(String title, String ingredients, String instructions, int rating) {
    this.title = title;
    this.ingredients = ingredients;
    this.instructions = instructions;
    this.rating = rating;
  }

  public String getTitle() {
    return title;
  }

  public String getIngredients(){
    return ingredients;
  }

  public String getInstructions(){
    return instructions;
  }

  public int getId() {
    return id;
  }

  public int getRating(){
    return rating;
  }


  // public void complete(){
  //   if (this.getIsCompleted() ){
  //     this.is_completed = false;
  //   } else {
  //     this.is_completed = true ;
  //   }
  //
  //   try(Connection con = DB.sql2o.open()) {
  //     String sql = "UPDATE recipes SET is_completed = :is_completed WHERE id = :id";
  //     con.createQuery(sql)
  //       .addParameter("is_completed", this.is_completed)
  //       .addParameter("id", this.id)
  //       .executeUpdate();
  //   }
  // }

  public static List<Recipe> all() {
    String sql = "SELECT * FROM recipes";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Recipe.class);
    }
  }

  public static List<Recipe> allRated() {
    String sql = "SELECT * FROM recipes WHERE rating > 0 ORDER BY rating DESC;";
    try(Connection con = DB.sql2o.open()){
      return con.createQuery(sql).executeAndFetch(Recipe.class);
    }
  }
  public static List<Recipe> searchIngredients(String search) {
    String sql = "SELECT * FROM recipes WHERE ingredients LIKE (:%search%);";
    Recipe recipe = con.createQuery(sql)
      .addParameter("search", search);
    try(Connection con = DB.sql2o.open()){
      return con.createQuery(sql).executeAndFetch(Recipe.class);
    }
  }

  @Override
  public boolean equals(Object otherRecipe){
    if (!(otherRecipe instanceof Recipe)) {
      return false;
    } else {
      Recipe newRecipe = (Recipe) otherRecipe;
      return this.getTitle().equals(newRecipe.getTitle()) &&
             this.getId() == newRecipe.getId();
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO recipes(title, ingredients, instructions, rating) VALUES (:title, :ingredients, :instructions, :rating)";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("title", this.title)
        .addParameter("ingredients", this.ingredients)
        .addParameter("instructions", this.instructions)
        .addParameter("rating", this.rating)
        .executeUpdate()
        .getKey();
    }
  }

  public static Recipe find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM recipes where id=:id";
      Recipe recipe = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Recipe.class);
      return recipe;
    }
  }

  public void update(String newTitle, String newIngredients, String newInstructions, int newRating) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE recipes SET title = :title, ingredients = :ingredients,  instructions = :instructions, rating = :rating WHERE id = :id";
      con.createQuery(sql)
        .addParameter("title", newTitle)
        .addParameter("ingredients", newIngredients)
        .addParameter("instructions", newInstructions)
        .addParameter("rating", newRating)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }


  public void addCategory(Category category) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO categories_recipes (category_id, recipe_id) VALUES (:category_id, :recipe_id)";
      con.createQuery(sql)
        .addParameter("category_id", category.getId())
        .addParameter("recipe_id", this.getId())
        .executeUpdate();
    }
  }


  public List<Category> getCategories(){
    try(Connection con = DB.sql2o.open()) {
      String joinQuery = "SELECT category_id FROM categories_recipes WHERE recipe_id = :recipe_id";
      List<Integer> categoryIds = con.createQuery(joinQuery)
        .addParameter("recipe_id", this.getId())
        .executeAndFetch(Integer.class);


      List<Category> categories = new ArrayList<Category>();

      for (Integer categoryId : categoryIds) {
        String categoryQuery = "Select * FROM categories WHERE id = :categoryId";
        Category category = con.createQuery(categoryQuery)
          .addParameter("categoryId", categoryId)
          .executeAndFetchFirst(Category.class);
        categories.add(category);
      }

      return categories;
    }
  }

  public void delete(){
    try(Connection con = DB.sql2o.open()) {
      String deleteQuery = "DELETE FROM recipes WHERE id = :id;";
        con.createQuery(deleteQuery)
          .addParameter("id", this.getId())
          .executeUpdate();

      String joinDeleteQuery = "DELETE FROM categories_recipes WHERE recipe_id = :recipeId";
        con.createQuery(joinDeleteQuery)
          .addParameter("recipeId", this.getId())
          .executeUpdate();
    }
  }

}
