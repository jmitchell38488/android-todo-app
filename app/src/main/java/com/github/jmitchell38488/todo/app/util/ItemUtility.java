package com.github.jmitchell38488.todo.app.util;

import com.github.jmitchell38488.todo.app.data.model.TodoItem;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by justinmitchell on 12/11/2016.
 */

public class ItemUtility {

    public static void reorderTodoItemList(List<TodoItem> list) {
        Object mLock = false;
        synchronized (mLock) {
            Collections.sort(list, new Comparator<TodoItem>() {
                @Override
                public int compare(TodoItem lhs, TodoItem rhs) {
                    return (lhs.isCompleted() == rhs.isCompleted() ? 0 : rhs.isCompleted() ? -1 : 1);
                }
            });
        }
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
        }

        return "";
    }

}
