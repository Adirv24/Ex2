import java.util.Stack;

public class SCell implements Cell {
    private String line;
    private int type;
    private int order;


    public SCell(String s) {
        setData(s);
        this.order = 0;
    }

    @Override
    public String getData() {
        return line;
    }

    @Override
    public void setData(String s) {
        this.line = s;
        this.type = identifyCellTypeBasedOnContent(s);
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int t) {
        this.type = t;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void setOrder(int t) {
        this.order = t;
    }

    @Override
    public String toString() {
        return getData();
    }

    private int identifyCellTypeBasedOnContent(String data) {
        if (data == null || data.isEmpty()) {
            return Ex2Utils.TEXT;
        }
        if (checkIfContentIsNumeric(data)) {
            return Ex2Utils.NUMBER;
        }
        if (checkIfContentIsFormula(data)) {
            return Ex2Utils.FORM;
        }
        return Ex2Utils.TEXT;
    }

    private boolean checkIfContentIsNumeric(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean checkIfContentIsFormula(String text) {
        if (text == null || !text.startsWith("=")) {
            return false;
        }
        String formula = text.substring(1);
        return formula.matches("[A-Za-z0-9()+\\-*/.]+") && checkIfParenthesesAreBalanced(formula);
    }

    private boolean checkIfParenthesesAreBalanced(String formula) {
        int balance = 0;
        for (char c : formula.toCharArray()) {
            if (c == '(') {
                balance++;
            } else if (c == ')') {
                balance--;
                if (balance < 0) {
                    return false;
                }
            }
        }
        return balance == 0;
    }

    private static boolean checkIfCharacterIsOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }


    private static int getOperatorPrecedenceLevel(char operator) {
        if (operator == '+' || operator == '-') {
            return 1;
        } else if (operator == '*' || operator == '/') {
            return 2;
        }
        return -1;
    }

    private static Double performOperationBasedOnOperator(char operator, Double b, Double a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return a / b;
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }
    public static Double computeFormula(String formula) {
        if (formula == null || !formula.startsWith("=")) {
            return null;
        }

        String expression = formula.substring(1);

        try {
            return evaluateExpression(expression);
        } catch (Exception e) {
            return null;
        }
    }

    private static Double evaluateExpression(String expression) {
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() &&
                        (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i));
                    i++;
                }
                i--;
                values.push(Double.parseDouble(sb.toString()));
            }
            else if (c == '(') {
                operators.push(c);
            }
            else if (c == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    values.push(performOperationBasedOnOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop();
            }
            else if (checkIfCharacterIsOperator(c)) {
                while (!operators.isEmpty() && getOperatorPrecedenceLevel(c) <= getOperatorPrecedenceLevel(operators.peek())) {
                    values.push(performOperationBasedOnOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(c);
            }
        }

        while (!operators.isEmpty()) {
            values.push(performOperationBasedOnOperator(operators.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

}
