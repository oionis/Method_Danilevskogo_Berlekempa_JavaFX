package sample;


import java.beans.IntrospectionException;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Window;

import sample.Classes.*;

import java.awt.image.BufferedImage;
import java.io.File;

//import com.intellij.ide.ui.AppearanceOptionsTopHitProvider;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;


public class Controller {

    public static DM task;

    @FXML
    private AnchorPane AnchorPane_1;

    @FXML
    private Button buttonChoseFile;

    @FXML
    private TextArea textArea;

    @FXML
    private Button buttonCreateCharPol;

    @FXML
    private TextField characterPolynomField;

    @FXML
    private TextArea textAreaBerlecamp;

    @FXML
    private Button buttonSaveResault;

    @FXML
    private ImageView polynomView;

    @FXML
    private TextArea textAreaPolynom;

    @FXML
    private Button buttonOkBerlekamp;

    @FXML
    private TextField textPolynom1;

    @FXML
    private Button buttonMultiplyPolynom;

    @FXML
    private TextField textPolynom2;

    @FXML
    private Button buttonKroneker;

    @FXML
    private Button buttonSaveBerlekemp;

    @FXML
    private Button buttonApply;

    public static int height = 800;
    public static int width = 800;
    TeXFormula polynomTeX = new TeXFormula();
    public static final boolean TEX_REVERSE = true;


    @FXML
    void initialize() {


        final FileChooser fileChooser = new FileChooser();


        buttonChoseFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Window primaryStage = null;
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null)
                    try {
                        Scanner textScan = new Scanner(file);
                        String text = "";
                        while (textScan.hasNextLine()) {
                            String line = textScan.nextLine();
                            text += line + "\n";
                        }
                        textArea.setText(text);
                        readScanner(new Scanner(file));

                    } catch (FileNotFoundException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Файл не найден.");
                        alert.showAndWait();
                    }

            }
        });

        buttonCreateCharPol.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String text = textArea.getText();

                Scanner scanner = new Scanner(text);

                readScanner(scanner);

                buttonOkBerlekamp.setDisable(false);
                buttonSaveResault.setDisable(false);
            }
        });

        buttonSaveResault.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Window primaryStage = null;
                FileChooser fc = new FileChooser();
                fc.setTitle("Сохранить отчёт");
                try {
                    fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
                    File path = fc.showSaveDialog(primaryStage);
                    try {
                        PrintWriter pw = new PrintWriter(path);
                        pw.println(task.getDebug());
                        pw.close();
                    } catch (java.io.FileNotFoundException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Файл не найден.");
                        alert.showAndWait();
                    }
                } catch (java.lang.NullPointerException e) {
                }
            }
        });

        buttonOkBerlekamp.setOnAction(event -> {
            Integer[] tempArr = new Integer[task.A[0].length + 1];
            tempArr[0] = 1;
            for (int i = 1; i < tempArr.length; i++) {
                tempArr[i] = (int) task.A[0][i - 1];
            }
            printBerlekemp(tempArr);

            buttonSaveBerlekemp.setDisable(false);
        });

        buttonMultiplyPolynom.setOnAction(event -> {


            Integer[] polynom1 = spliter(textPolynom1.getText());
            Integer[] polynom2 = spliter(textPolynom2.getText());

            PolyF2 p1 = new PolyF2(new ArrayList<Integer>(Arrays.asList(polynom1)));
            PolyF2 p2 = new PolyF2(new ArrayList<Integer>(Arrays.asList(polynom2)));
            PolyF2 p3 = p1.multiply(p1, p2);


            String polynom = "";
            Integer[] tempArr = new Integer[p3.coefficients.size()];
            for (int i = 0; i < p3.coefficients.size(); i++) {
                polynom += p3.coefficients.get(i);
                tempArr[i] = p3.coefficients.get(i);
            }

            textAreaPolynom.setText(polynom);

            long[][] arr = buildMatrix(Integer.parseInt(polynom));
            textArea.setText(MatrixToString(arr));
            //printBerlekemp(tempArr);

            buttonCreateCharPol.setDisable(false);
            buttonOkBerlekamp.setDisable(true);
            buttonSaveBerlekemp.setDisable(true);
            buttonSaveResault.setDisable(true);
            textAreaBerlecamp.setText("");
            polynomView.setImage(null);

        });

        buttonApply.setOnAction(actionEvent -> {

            long[][] arr = buildMatrix(Integer.parseInt(textAreaPolynom.getText()));
            textArea.setText(MatrixToString(arr));


            buttonCreateCharPol.setDisable(false);
            buttonOkBerlekamp.setDisable(true);
            buttonSaveBerlekemp.setDisable(true);
            buttonSaveResault.setDisable(true);
            textAreaBerlecamp.setText("");
            polynomView.setImage(null);
        });

        buttonKroneker.setOnAction(actionEvent -> {
            long[][] matrix = Kroneker(textPolynom1.getText(), textPolynom2.getText());
            textArea.setText(MatrixToString(matrix));

            buttonCreateCharPol.setDisable(false);
            buttonOkBerlekamp.setDisable(true);
            buttonSaveBerlekemp.setDisable(true);
            buttonSaveResault.setDisable(true);
            textAreaBerlecamp.setText("");
            polynomView.setImage(null);
            textAreaPolynom.setText("");

        });

    }


    private void readScanner(Scanner scanner) {
        int i = 0;
        List<Double> rn = new ArrayList<>();

        while (scanner.hasNextDouble()) {
            rn.add(scanner.nextDouble());
        }

        double size = (double) rn.size();
        int n = (int) Math.round(Math.sqrt(size));
        if (n * n > rn.size()) n--;

        task = new DM(rn.subList(0, n * n), n);
        //String tempStr = "";

        Integer[] tempArr = new Integer[task.A[0].length];
        for (int j = 0; j < task.A[0].length; j++) {
            tempArr[j] = (int) task.A[0][j];
            //tempStr += tempArr[j] + " ";
        }

        //textAreaBerlecampPolynom.setText(tempStr);
        //printBerlekemp(tempArr);

        buttonSaveResault.setDisable(false);
        makePolynom(task.formatTex(TEX_REVERSE));


    }

    private void printBerlekemp(Integer[] tempArr) {

        PolyF2 p = new PolyF2(new ArrayList<Integer>(Arrays.asList(tempArr)));

        String tempStr = "";
        int k = 1;
        for (PolyF2 x : Berlecamp.factor(p)) {

            tempStr += k + ".  F(x) = " + Berlecamp.printResault(x) + "\n";
            k++;
        }

        textAreaBerlecamp.setText(tempStr);
    }

    private void makePolynom(String latex) {
        this.polynomTeX.setLaTeX("F(x) = " + latex);
        java.awt.Image awtImage = polynomTeX.createBufferedImage(TeXConstants.ALIGN_CENTER, 24, java.awt.Color.BLACK, null);
        Image fxImage = SwingFXUtils.toFXImage((BufferedImage) awtImage, null);
        polynomView.setImage(fxImage);
    }

    private static int nod(int a, int b) {
        return b == 0 ? a : nod(b, a % b);
    }

    public static int nok(int a, int b) {
        return a / nod(a, b) * b;
    }

    private static long[][] buildMatrix(int polynom) {
        long[] arr = numberDivision(polynom); // записали полином поэдементно в масив
        long[][] arr2 = new long[arr.length - 1][arr.length - 1];

        for (int i = 0; i < arr.length - 1; i++) {
            arr2[0][i] = arr[i + 1];
        }

        for (int i = 1; i < arr2.length; i++) {
            for (int j = 0; j < arr2[i].length; j++) {
                if (i - 1 == j) {
                    arr2[i][j] = 1;
                } else
                    arr2[i][j] = 0;

            }
        }
        return arr2;
    }

    private static long[] numberDivision(long polynom) {
        long[] arr = new long[getCountsOfDigits(polynom)];
        for (int i = arr.length - 1; i >= 0; i--) {
            arr[i] = polynom % 10;
            polynom /= 10;
        }
        return arr;
    }

    private static int getCountsOfDigits(long number) {
        return (number == 0) ? 1 : (int) Math.ceil(Math.log10(Math.abs(number) + 0.5));
    }

    private static String toBinary(long[] arr) {
        String temp;
        String str = Integer.toBinaryString((int) arr[0]);

        for (int i = 0; i < arr.length; i++) {
            temp = Integer.toBinaryString((int) arr[i]);
            if (getCountsOfDigits(Integer.parseInt(temp)) == 1 && i != 0) {
                temp = "00" + temp;
            } else if (getCountsOfDigits(Integer.parseInt(temp)) == 2) {
                temp = "0" + temp;
            }
            if (i != 0) {
                str += temp;
            }
        }
        return str;
    }

    public static void printMatrix(long[][] arr2) {
        for (long[] longs : arr2) {
            for (long aLong : longs) {
                System.out.print("\t" + aLong);
            }
            System.out.println("");
        }
    }

    private static String MatrixToString(long[][] arr2) {
        String matrixStr = "";
        for (long[] longs : arr2) {
            for (long aLong : longs) {
                matrixStr += "\t " + aLong;
            }
            matrixStr += "\n";
        }
        return matrixStr;
    }

    private static Integer[] spliter(String str) {
        int temp = Integer.parseInt(str);
        Integer[] arr = new Integer[str.length()];
        for (int i = arr.length - 1; i >= 0; i--) {
            arr[i] = temp % 10;
            temp /= 10;
        }
        return arr;
    }

    private static long[][] Kroneker(String str1, String str2) {
        long[][] matrix1 = buildMatrix(Integer.parseInt(str1));
        long[][] matrix2 = transp(buildMatrix(Integer.parseInt(str2)));

        System.out.println("\n \nMatrix A: \n");

        printMatrix(matrix1);
        System.out.println("\n Matrix B: \n");
        printMatrix(matrix2);

//        long[][] matrix1 = {
//                {1,2,3},
//                {4,5,6},
//                {7,8,9}
//        };
//        long[][] matrix1 = {
//                {1, 2},
//                {3, 4},
//        };
//        long[][] matrix2 = {
//                {1, 2, 3},
//                {4, 5, 6},
//                {7, 8, 9}
//        };

        long[][] resault = new long[matrix1.length * matrix2.length][matrix1.length * matrix2.length];
        int a = 0;
        int b = 0;
        int c = 0;
        int d = 0;
        int k = 1;

        for (int i = 0; i < resault.length; i++) {
            for (int j = 0; j < resault.length; j++) {
                resault[i][j] = matrix1[a][b] * matrix2[c][d];


                if ((k % matrix2.length) == 0) {
                    b++;
                }

                if ((k % (matrix2.length * matrix1.length)) == 0) {
                    c++;
                }
                if ((k % matrix2.length) == 0) {
                    d = -1;
                }
                if ((k % (matrix1.length * matrix2.length)) == 0) {
                    b = 0;
                }
                if ((k % (matrix1.length * matrix2.length * matrix2.length)) == 0) {
                    a++;
                    c = 0;
                }
                k++;
                d++;

            }
        }

        return resault;
    }

    private static long[][] transp(long[][] matrix) {
        long[][] newMatrix = new long[matrix.length][matrix.length];

        for (int i = 0; i < newMatrix.length; i++) {
            for (int j = 0; j < newMatrix.length; j++) {
                newMatrix[i][j] = matrix[j][i];
            }
        }
        return newMatrix;
    }

    public static void buildGrid(int sizeA, int sizeB, long[][] arr, AnchorPane AnchorPane, GridPane gridMatrix) {

        gridMatrix.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        for (int i = 0; i < sizeA; i++) {
            ColumnConstraints column = new ColumnConstraints(40);
            gridMatrix.getColumnConstraints().add(column);
        }

        for (int i = 0; i < sizeB; i++) {
            RowConstraints row = new RowConstraints(40);
            gridMatrix.getRowConstraints().add(row);
        }

        for (int i = 0; i < sizeA; i++) {
            for (int j = 0; j < sizeB; j++) {
                Label label = new Label("" + arr[j][i]);
                label.getStyleClass().add("game-grid-cell");
                gridMatrix.add(label, i, j);
            }
        }
        gridMatrix.setLayoutX((width - sizeA * 40) / 2);
        gridMatrix.setLayoutY(70);
        AnchorPane.getChildren().add(gridMatrix);
        gridMatrix.setGridLinesVisible(true);
    }

}

