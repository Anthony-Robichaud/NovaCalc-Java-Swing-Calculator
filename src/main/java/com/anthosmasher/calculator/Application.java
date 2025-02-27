package com.anthosmasher.calculator;

import javax.script.ScriptException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Application extends JFrame implements ActionListener {

    JTextField equationField;
    JButton[] numberButtons = new JButton[10];
    JButton[] functionButtons = new JButton[7];

    ImageIcon[] icons = new ImageIcon[17];

    DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.CANADA);
    DecimalFormat df = new DecimalFormat("0.##", dfs);

    public Application() {
        this.setLayout(null);

        createButtonIcons();
        createNumberButtons();
        createFunctionButtons();

        this.setTitle("NovaCalc");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 650);
        this.setResizable(false);
        this.setVisible(true);

        equationField = new JTextField();
        equationField.setBounds(50, 20, 350, 110);
        equationField.setFont(loadCustomFont("arcade.ttf", 35));
        equationField.setBackground(Color.BLACK);
        equationField.setForeground(Color.GREEN);
        equationField.setHorizontalAlignment(JTextField.RIGHT);
        equationField.setEditable(false);
        equationField.setVisible(true);
        equationField.setBorder(null);
        this.add(equationField);

        for (int i = 0; i < 10; i++) {
            numberButtons[i].addActionListener(this);
        }

        for (int i = 0; i < 7; i++) {
            functionButtons[i].addActionListener(this);
        }

        URL iconURL = getClass().getResource("/calculator.png");
        ImageIcon icon = new ImageIcon(iconURL);
        this.setIconImage(icon.getImage());
        this.getContentPane().setBackground(Color.DARK_GRAY);
    }

    public void createButtonIcons() {
        for (int i = 0; i < 10; i++) {
            URL numberButtonsIconsURLs = getClass().getResource("/calculator_icons/" + i + ".png");

            icons[i] = new ImageIcon(numberButtonsIconsURLs);
        }

        for (int i = 0; i < 7; i++) {
            URL functionButtonsIconsURLs = getClass().getResource("/calculator_icons/operator" + i + ".png");

            icons[10 + i] = new ImageIcon(functionButtonsIconsURLs);
        }
    }

    public void createNumberButtons() {
        int buttonSize = 110;
        int spacing = 10;
        int startX = 50;
        int startY = 140;

        int[][] buttonOrder = {
            {7, 8, 9},
            {4, 5, 6},
            {1, 2, 3},
            {0}
        };

        for (int i = 0; i < buttonOrder.length; i++) {
            for (int j = 0; j < buttonOrder[i].length; j++) {
                int num = buttonOrder[i][j];

                numberButtons[num] = new JButton();
                numberButtons[num].setIcon(icons[num]);

                int x = startX + j * (buttonSize + spacing);
                int y = startY + i * (buttonSize + spacing);

                numberButtons[num].setBounds(x, y, buttonSize, buttonSize);

                this.add(numberButtons[num]);
            }
        }
    }

    public void createFunctionButtons() {
        String[] operators = {"+", "-", "*", "/"};

        int buttonSize = 110;
        int spacing = 10;
        int startX = 50 + 3 * (buttonSize + spacing) + spacing;
        int startY = 140;

        for (int i = 0; i < operators.length; i++) {
            functionButtons[i] = new JButton();
            functionButtons[i].setIcon(icons[10 + i]);
            functionButtons[i].setBounds(startX, startY + i * (buttonSize + spacing), buttonSize, buttonSize);
            this.add(functionButtons[i]);
        }

        functionButtons[4] = new JButton();
        functionButtons[4].setIcon(icons[14]);
        functionButtons[4].setBounds(startX, startY - (buttonSize + spacing), buttonSize, buttonSize);
        this.add(functionButtons[4]);

        functionButtons[5] = new JButton();
        functionButtons[5].setIcon(icons[15]);
        functionButtons[5].setBounds(50 + buttonSize + spacing, 140 + 3 * (buttonSize + spacing), buttonSize, buttonSize);
        this.add(functionButtons[5]);

        functionButtons[6] = new JButton();
        functionButtons[6].setIcon(icons[16]);
        functionButtons[6].setBounds(50 + 2 * (buttonSize + spacing), 140 + 3 * (buttonSize + spacing), buttonSize, buttonSize);
        this.add(functionButtons[6]);
    }

    private Font loadCustomFont(String filepath, float size) {
        try {
            InputStream fontfile = getClass().getResourceAsStream("/" + filepath);
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontfile);
            return font.deriveFont(size);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, 70);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < 10; i++) {
            if (e.getSource() == numberButtons[i]) {
                equationField.setText(equationField.getText() + i);
            }
        }

        if (e.getSource() == functionButtons[0]) {
            equationField.setText(equationField.getText() + "+");
        }

        if (e.getSource() == functionButtons[1]) {
            equationField.setText(equationField.getText() + "-");
        }

        if (e.getSource() == functionButtons[2]) {
            equationField.setText(equationField.getText() + "*");
        }

        if (e.getSource() == functionButtons[3]) {
            equationField.setText(equationField.getText() + "/");
        }

        if (e.getSource() == functionButtons[4]) {
            equationField.setText("");
        }

        if (e.getSource() == functionButtons[5]) {
            equationField.setText(equationField.getText() + ".");
        }

        if (e.getSource() == functionButtons[6]) {
            String equation = equationField.getText();
            double result = Double.parseDouble(evaluate(equation));

            equationField.setText(df.format(result));
        }
    }

    public String evaluate(String equation) {
        try {
            return String.valueOf(parse(equation));
        } catch (Exception e) {
            return "Error";
        }
    }

    public double parse(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                return x;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }
        }.parse();
    }
}
