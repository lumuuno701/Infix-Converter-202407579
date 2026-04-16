import java.util.*;

/**
 * InfixConverter
 * Converts an infix expression to both Postfix and Prefix notations.
 * Uses the Shunting-Yard algorithm by Edsger Dijkstra.
 */
public class InfixConverter {

    // ── Operator precedence ─────────────────────
    private static int precedence(char op) {
        switch (op) {
            case '^': return 3;
            case '*': case '/': return 2;
            case '+': case '-': return 1;
            default: return -1;
        }
    }

    private static boolean isOperand(char c) {
        return Character.isLetterOrDigit(c);
    }

    // ── Infix → Postfix (Shunting-Yard) ─────────
    public static String infixToPostfix(String expr) {
        StringBuilder output = new StringBuilder();
        Deque<Character> stack = new ArrayDeque<>();

        for (int i = 0; i < expr.length(); i++) {
            char token = expr.charAt(i);
            if (token == ' ') continue;

            if (isOperand(token)) {
                output.append(token).append(' ');

            } else if (token == '(') {
                stack.push(token);

            } else if (token == ')') {
                while (!stack.isEmpty() && stack.peek() != '(')
                    output.append(stack.pop()).append(' ');
                if (!stack.isEmpty()) stack.pop(); // discard '('

            } else { // operator
                while (!stack.isEmpty()
                        && stack.peek() != '('
                        && (precedence(stack.peek()) > precedence(token)
                            || (precedence(stack.peek()) == precedence(token)
                                && token != '^'))) {
                    output.append(stack.pop()).append(' ');
                }
                stack.push(token);
            }
        }
        while (!stack.isEmpty())
            output.append(stack.pop()).append(' ');

        return output.toString().trim();
    }

    // ── Infix → Prefix (reverse trick) ──────────
    public static String infixToPrefix(String expr) {
        // 1. Reverse the expression and swap parentheses
        StringBuilder rev = new StringBuilder(expr).reverse();
        for (int i = 0; i < rev.length(); i++) {
            if      (rev.charAt(i) == '(') rev.setCharAt(i, ')');
            else if (rev.charAt(i) == ')') rev.setCharAt(i, '(');
        }
        // 2. Get postfix of reversed expression
        String postfixOfRev = infixToPostfixForPrefix(rev.toString());
        // 3. Reverse again → Prefix
        return new StringBuilder(postfixOfRev).reverse().toString().trim();
    }

    /** Variant used internally by infixToPrefix: '^' is left-associative */
    private static String infixToPostfixForPrefix(String expr) {
        StringBuilder output = new StringBuilder();
        Deque<Character> stack = new ArrayDeque<>();

        for (int i = 0; i < expr.length(); i++) {
            char token = expr.charAt(i);
            if (token == ' ') continue;

            if (isOperand(token)) {
                output.append(token).append(' ');
            } else if (token == '(') {
                stack.push(token);
            } else if (token == ')') {
                while (!stack.isEmpty() && stack.peek() != '(')
                    output.append(stack.pop()).append(' ');
                if (!stack.isEmpty()) stack.pop();
            } else {
                while (!stack.isEmpty()
                        && stack.peek() != '('
                        && precedence(stack.peek()) >= precedence(token)) {
                    output.append(stack.pop()).append(' ');
                }
                stack.push(token);
            }
        }
        while (!stack.isEmpty())
            output.append(stack.pop()).append(' ');

        return output.toString();
    }

    // ── Main ─────────────────────────────────────
    public static void main(String[] args) {
        String[] tests = {
            "A+B*C-D/E",
            "(A+B)*(C-D)",
            "A^B^C",
            "((A+B)*C-(D-E))^(F+G)"
        };

        System.out.printf("%-30s %-30s %-30s%n",
                "Infix", "Postfix", "Prefix");
        System.out.println("-".repeat(92));

        for (String expr : tests) {
            System.out.printf("%-30s %-30s %-30s%n",
                    expr,
                    infixToPostfix(expr),
                    infixToPrefix(expr));
        }
    }
}