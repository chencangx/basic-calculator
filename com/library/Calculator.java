package com.library;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Scanner;

public class Calculator {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String expression = null;
    StringBuilder output = new StringBuilder(50);
    String horizontal =
        "\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500";
    double result = 0;
    while (true) {
      System.out.println("Type the math expression");
      expression = sc.nextLine();
      if (expression.equals("exit")) {
        System.out.println("Exited");
        System.exit(0);
      }
      result = eval(expression);
      setSysClipboardText(Double.toString(result));
      output.append("Result:\n").append(result).append('\n').append(horizontal);
      System.out.println(output);
      output.setLength(0);
    }
  }

  public static double eval(final String str) {
    return new Object() {
      int pos = -1, ch;

      void nextChar() {
        ch = (++pos < str.length()) ? str.charAt(pos) : -1;
      }

      boolean eat(int charToEat) {
        while (ch == ' ')
          nextChar();
        if (ch == charToEat) {
          nextChar();
          return true;
        }
        return false;
      }

      double parse() {
        nextChar();
        double x = parseExpression();
        if (pos < str.length())
          throw new RuntimeException("Unexpected: " + (char) ch);
        return x;
      }

      double parseExpression() {
        double x = parseTerm();
        for (;;) {
          if (eat('+'))
            x += parseTerm(); 
          else if (eat('-'))
            x -= parseTerm(); 
          else
            return x;
        }
      }

      double parseTerm() {
        double x = parseFactor();
        for (;;) {
          if (eat('*'))
            x *= parseFactor();
          else if (eat('/'))
            x /= parseFactor();
          else
            return x;
        }
      }

      double parseFactor() {
        if (eat('+'))
          return parseFactor();
        if (eat('-'))
          return -parseFactor();

        double x;
        int startPos = this.pos;
        if (eat('(')) {
          x = parseExpression();
          eat(')');
        } else if ((ch >= '0' && ch <= '9') || ch == '.') { 
          while ((ch >= '0' && ch <= '9') || ch == '.')
            nextChar();
          x = Double.parseDouble(str.substring(startPos, this.pos));
        } else if (ch >= 'a' && ch <= 'z') {
          while (ch >= 'a' && ch <= 'z')
            nextChar();
          String func = str.substring(startPos, this.pos);
          x = parseFactor();
          if (func.equals("sqrt"))
            x = Math.sqrt(x);
          else if (func.equals("sin"))
            x = Math.sin(Math.toRadians(x));
          else if (func.equals("cos"))
            x = Math.cos(Math.toRadians(x));
          else if (func.equals("tan"))
            x = Math.tan(Math.toRadians(x));
          else
            throw new RuntimeException("Unknown function: " + func);
        } else {
          throw new RuntimeException("Unexpected: " + (char) ch);
        }

        if (eat('^'))
          x = Math.pow(x, parseFactor());
        return x;
      }
    }.parse();
  }

  public static final void setSysClipboardText(String result) {
    StringSelection contents = new StringSelection(result);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(contents, contents);
  }
}
