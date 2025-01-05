package com.cgvsu.obj_io.writer;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.obj_io.reader.ObjReader;
import com.cgvsu.obj_io.reader.ObjReaderException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

class ObjReaderTest {

    @Test
    public void testParseVertex01() {
        String objVer = "v 1.01 1.02 1.03";
        Model model = ObjReader.read(objVer);
        Vector3f result = model.vertices.get(0);
        Vector3f expectedResult = new Vector3f(1.01f, 1.02f, 1.03f);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testParseVertex02() {
        String objVer = "v 1.01 1.02 1.03";
        Model model = ObjReader.read(objVer);
        Vector3f result = model.vertices.get(0);
        Vector3f expectedResult = new Vector3f(1.01f, 1.02f, 1.10f);
        Assertions.assertFalse(result.equals(expectedResult));
    }

    @Test
    public void testParseVertex03() {
        String objVer = "v ab o ba";
        try {
            Model model = ObjReader.read(objVer);
        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 0. jshaper: parse: invalid float format";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testParseVertex04() {
        String objVer = "v 1.0 2.0";
        try {
            Model model = ObjReader.read(objVer);
        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 0. jshaper: parse: invalid vertex format";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testParseVertex05() {
        // АГААА! Вот тест, который говорит, что у метода нет проверки на более, чем 3 числа
        // А такой случай лучше не игнорировать, а сообщать пользователю, что у него что-то не так
        // ассерт, чтобы не забыть про тест:

        /** Ответ: Используемая библиотека jshaper поддерживает 4D вершины, поэтому тест теперь имеет другой смысл **/


        String objVer = "v 1.0 2.0 3.0 4.0";
        try {
            Model model = ObjReader.read(objVer);
        } catch (ObjReaderException exception) {
            String expectedError = "THERE'S NO ERROR, BECAUSE THE LIBRARY ACTUALLY SUPPORTS 4D VERTICES, LOL";
            Assertions.assertEquals(expectedError, exception.getMessage());

            Assertions.fail("Exception was thrown, but 4D vertices should be supported."); // Для облегчения понимания, при ошибке
        }
    }
}