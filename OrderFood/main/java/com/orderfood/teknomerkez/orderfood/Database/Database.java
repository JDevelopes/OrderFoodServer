package com.orderfood.teknomerkez.orderfood.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.orderfood.teknomerkez.orderfood.Model.Favorites;
import com.orderfood.teknomerkez.orderfood.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "orderFoodDB.db";
    private static final int DB_VERSION = 2;

    public Database(Context context) {

        super(context, DB_NAME, context.getExternalFilesDir(null).getAbsolutePath(), null, DB_VERSION);
    }

    public boolean checkFoodExist(String foodID, String userID) {
        boolean flag = false;
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = null;
        String SQLQurey = String.format("SELECT * FROM OrderDetail WHERE UserID='%s' AND ProductID='%s'", userID, foodID);
        cursor = database.rawQuery(SQLQurey, null);
        if (cursor.getCount() > 0) {
            flag = true;
        } else {
            flag = false;
        }
        cursor.close();
        return flag;
    }

    public void increaseCart(String userID, String foodID) {
        SQLiteDatabase db = getReadableDatabase();
        String increase = String.format("UPDATE OrderDetail SET Quantity= Quantity+1 WHERE UserID = '%s' AND ProductID='%s'", userID, foodID);
        db.execSQL(increase);
    }

    public List<Order> getCarts(String userID) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"UserID", "ProductName", "ProductID", "Quantity", "Price", "Discount", "Image"};
        String sqlTable = "OrderDetail";
        qb.setTables(sqlTable);

        Cursor c = qb.query(db, sqlSelect, "UserID=?", new String[]{userID}, null, null, null, null);

        final List<Order> result = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                result.add(new Order
                        (c.getString(c.getColumnIndex("UserID")),
                                c.getString(c.getColumnIndex("ProductID")),
                                c.getString(c.getColumnIndex("ProductName")),
                                c.getString(c.getColumnIndex("Quantity")),
                                c.getString(c.getColumnIndex("Price")),
                                c.getString(c.getColumnIndex("Discount")),
                                c.getString(c.getColumnIndex("Image"))));
            } while (c.moveToNext());
        }
        return result;
    }

    public void addToCart(Order order) {
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("INSERT OR REPLACE INTO OrderDetail(UserID,ProductID,ProductName,Quantity,Price,Discount,Image) VALUES('%s','%s','%s','%s','%s','%s','%s')",
                order.getUserID(),
                order.getProductID(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage());
        db.execSQL(query);

    }

    public void CleanCart(String userID) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserID='%s'", userID);
        db.execSQL(query);
    }

    public int getCountCart(String userID) {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM OrderDetail Where UserID='%s'", userID);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                count = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return count;
    }

    public void updateCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity= '%s' WHERE UserID = '%s' AND ProductID='%s'", order.getQuantity(), order.getUserID(), order.getProductID());
        db.execSQL(query);
    }

    public void removeFromCart(String productID, String userID) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserID='%s' and ProductId='%s'", userID, productID);
        db.execSQL(query);
    }

    //Favorites
    public void addToFavorites(Favorites favorites) {
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("INSERT INTO Favorites(" +
                        "FoodId,FoodName,FoodPrice,FoodMenuId,FoodImage,FoodDiscount,FoodDescription,FoodUserId)" +
                        "VALUES('%s', '%s','%s', '%s','%s', '%s','%s', '%s');",
                favorites.getFoodId(),
                favorites.getFoodName(),
                favorites.getFoodPrice(),
                favorites.getFoodMenuId(),
                favorites.getFoodImage(),
                favorites.getFoodDiscount(),
                favorites.getFoodDescription(),
                favorites.getFoodUserId());
        db.execSQL(query);
        db.close();
    }

    public void removeFavorites(String foodId, String userID) {
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("DELETE FROM Favorites WHERE FoodId='%s' and FoodUserId='%s';", foodId, userID);
        db.execSQL(query);
        db.close();
    }

    public boolean isFavorite(String foodId, String userID) {
        SQLiteDatabase db = getReadableDatabase();
        //String query = "SELECT * FROM Favorites WHERE FoodId = ? and FoodUserId = ?";
        Cursor cursor = db.query("Favorites", new String[]{"FoodId", "FoodUserId"},
                "FoodId = ? and FoodUserId = ?", new String[]{foodId, userID},
                null, null, null);
        //        //Cursor cursor = db.rawQuery(query, new String[] {foodId, userID} );
        boolean isFavorite = cursor.getCount() == 1;
        cursor.close();
        return isFavorite;
    }

    public List<Favorites> getAllFavorites(String userID) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

            String[] sqlSelect =
                    {"FoodUserId", "FoodId", "FoodName", "FoodPrice", "FoodMenuId", "FoodImage", "FoodDiscount", "FoodDescription"};
            String sqlTable = "Favorites";
            qb.setTables(sqlTable);

            Cursor c = qb.query(db, sqlSelect, "FoodUserId=?", new String[]{userID}, null, null, null, null);

            final List<Favorites> result = new ArrayList<>();
            if (c.moveToFirst()) {
                do {
                    result.add(new Favorites(
                            c.getString(c.getColumnIndex("FoodUserId")),
                            c.getString(c.getColumnIndex("FoodId")),
                            c.getString(c.getColumnIndex("FoodName")),
                            c.getString(c.getColumnIndex("FoodPrice")),
                            c.getString(c.getColumnIndex("FoodMenuId")),
                            c.getString(c.getColumnIndex("FoodImage")),
                            c.getString(c.getColumnIndex("FoodDiscount")),
                            c.getString(c.getColumnIndex("FoodDescription"))
                    ));
                } while (c.moveToNext());
            }
            return result;
    }

}
