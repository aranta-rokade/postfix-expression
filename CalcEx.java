import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CalcEx {

    /**
     * Array to store the infix expressions read from the input file
     */
    String[] arrInfixExp;
    
    /**
     * Buffer to read input file
     */
    BufferedReader fileInputBuffer;

    /**
     * Stack Interface object
     */
    IStack stack;

    /**
     * Logger object to log the results to a text file
     */
    Logger logger;

    /**
     * Constructor: CalcEx()
     * 
     * Intialise class objects
     */
    public CalcEx() throws IOException {
        logger = new Logger("log.txt");
    }

    /**
     * Function : main() 
     * calls the methods to convert expressions to infix and then to evaluate them
     */
    public static void main(String[] args) throws IOException {
        CalcEx obj = new CalcEx();
        if (args.length > 0) {
            System.out.println(args[0]);
            obj.readFile(args[0]);
            if (obj.arrInfixExp != null) {
                obj.stack = new StackLinkedList();
                obj.logger.log("==================================");
                obj.logger.log("Stack - Linked List Implementation");
                obj.logger.log("==================================");
                obj.logger.log("");
                obj.parseArrayInfixToPostfix();
                obj.stack = new StackDyanamicArray();
                obj.logger.log("=====================================");
                obj.logger.log("Stack - Dyanamic Array Implementation");
                obj.logger.log("=====================================");
                obj.logger.log("");
                obj.parseArrayInfixToPostfix();
            }
        } else {
            System.out.println("Enter filepath as command line argument.");
        }
        obj.logger.terminate();
    }

    /**
     * Function: readFile(String path)
     * Read the input file into the arrays
     * input: path of the file to read
     */
    public void readFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists() || file.isDirectory()) {
            System.out.println("File Not Found.");
            return;
        }

        fileInputBuffer = new BufferedReader(new FileReader(path));
        String str;
        List<String> list = new ArrayList<String>();
        while ((str = fileInputBuffer.readLine()) != null && !str.trim().equals("")) {
            list.add(str.replace(" ", ""));
        }

        arrInfixExp = new String[list.size()];

        String item;
        for (int i = 0; i < list.size(); i++) {
            item = list.get(i);
            arrInfixExp[i] = item;
        }
    }

    /**
     * Function parseArrayInfixToPostfix()
     * calls the methods to perform the operstions for each infix expression
     */
    public void parseArrayInfixToPostfix() throws IOException {
        for (int i = 0; i < arrInfixExp.length; i++) {
            String postfixExp = infixToPostfix(arrInfixExp[i]);
            String result;
            if (postfixExp.equals("Invalid Expression")) {
                result = "Invalid Expression";
            } else {
                result = evaluatePostfixExp(postfixExp);
                if(result.equals("Invalid Expression") )
                    postfixExp = "Invalid Expression";
            }

            System.out.println("------------------------------------------------");
            System.out.println("Infix Expression: " + arrInfixExp[i]);
            logger.log("The expression as read from input : " + arrInfixExp[i]);
            System.out.println("Postfix Expression: " + postfixExp);
            logger.log("The expression in postfix notation : " + postfixExp);
            logger.log("Result of the expression: " + result);
            System.out.println("Result of the expression: " + result);
            logger.log("");
        }
    }
    /**
     * Fucntion: String infixToPostfix(String infixExp)
     * Convert an infix expression to postfix
     * input : String - infix expression
     * output: String - postfix expression
     */
    public String infixToPostfix(String infixExp) {
        String postfixExp = "";
        boolean numberStartFlag = false;
        if (!Character.isDigit(infixExp.charAt(0)) && infixExp.charAt(0) != '(') {
            return "Invalid Expression";
        }
        if (!Character.isDigit(infixExp.charAt(infixExp.length()-1)) && infixExp.charAt(infixExp.length()-1) != ')') {
            return "Invalid Expression";
        }
        try {
            for (int i = 0; i < infixExp.length(); i++) {
                char c = infixExp.charAt(i);
                System.out.println("Character: " + c);

                if (Character.isDigit(c)) {
                    postfixExp += c;
                    numberStartFlag = true;
                } else {
                    if (numberStartFlag) {
                        postfixExp += " ";
                    }
                    numberStartFlag = false;

                    if (c == '(') {
                        stack.push(String.valueOf(c));
                    } else if (c == ')') {
                        while (!stack.isEmpty() && !stack.peek().equals("(")) {
                            postfixExp += stack.pop();
                        }
                        if (!stack.isEmpty() && !stack.peek().equals("(")) {
                            System.out.println("Invalid Expression");
                            stack.makeEmpty();
                            return "Invalid Expression";
                        } else {
                            stack.pop();
                        }
                    } else {
                        if (operatorPrecedence(String.valueOf(c)) == -1) {
                            System.out.println("Invalid Expression");
                            stack.makeEmpty();
                            return "Invalid Expression";
                        }
                        while (!stack.isEmpty() && operatorPrecedence(String.valueOf(c)) <= operatorPrecedence(stack.peek())) {
                            postfixExp += stack.pop();
                        }
                        stack.push(String.valueOf(c));
                    }
                }
                System.out.println("Stack:");
                stack.print();
            }
            while (!stack.isEmpty()) {
                String popped = stack.pop();
                if (popped.equals("(")) {
                    stack.makeEmpty();
                    return "Invalid Expression";
                }
                postfixExp += popped;
            }

            System.out.println("Final Postfix Expression: " + postfixExp);
            return postfixExp;
        }
        catch(Exception e){
            return "Invalid Expression";
        }
    }

    /**
     * Function: int operatorPrecedence(String operator)
     * Gives the precendence of an operator
     * input : String - operator
     * output: int - value
     */
    public int operatorPrecedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            default:
                return -1;
        }
    }

    /**
     * Function: String evaluatePostfixExp(String postfixExp)
     * Evaluates the postfix expression mathematically
     * input: String - postfix expression
     * output: String - result of the expression
     */
    public String evaluatePostfixExp(String postfixExp) {
        boolean numberStartFlag = false;
        String operand = "";
        try{
            for (int i = 0; i < postfixExp.length(); i++) {
                char c = postfixExp.charAt(i);
                System.out.println("Character: " + c);

                // check if character is a digit 
                // if previous charcater was digit, append it to operand
                if (Character.isDigit(c)) {
                    numberStartFlag = true;
                    operand += c;
                } else {
                    // push the previously scanned operand
                    if (numberStartFlag) {
                        numberStartFlag = false;
                        stack.push(operand);
                        System.out.println("Operand: " + operand);
                        operand = "";
                    }
                    if (operatorPrecedence(String.valueOf(c)) != -1) {
                        double operand1 = Double.parseDouble(stack.pop());
                        double operand2 = Double.parseDouble(stack.pop());
                        double answer = 0.0;
                        switch (c) {
                            case '+':
                                answer = (operand2 + operand1);
                                break;
                            case '-':
                                answer = (operand2 - operand1);
                                break;
        
                            case '*':
                                answer = (operand2 * operand1);
                                break;
                            case '/':
                                if(operand1 == 0)
                                    throw new ArithmeticException();
                                answer = (operand2 / operand1);
                                break;
                            default:
                                return "Invalid Expression";
                        }
                        stack.push(String.valueOf(answer));
                    }
                }
            }
            return stack.pop();

        }
        catch(ArithmeticException e) {
            return "Divide by zero";
        }
        catch(Exception e){
            return "Invalid Expression";
        }
    }
}

/**
 * Class: StackDyanamicArray
 * Stack implementation using Dynamic arrays
 */
class StackDyanamicArray implements IStack {

    int top;
    int size;
    String[] array;

    public StackDyanamicArray() {
        top = -1;
        size = 80;
        array = new String[size];
    }

    public void push(String c) {
        if (isFull()) {
            String[] tmp = new String[size * 2];
            size *= 2; 
            System.arraycopy(array, 0, tmp, 0, array.length);
            array = tmp;
        }
        array[++top] = c;
    }

    public String pop() {
        return array[top--];
    }

    public String peek() {
        if (!isEmpty()) {
            return array[top];
        } else {
            return " ";
        }
    }

    public boolean isFull() {
        return (top == size - 1);
    }

    public boolean isEmpty() {
        return (top == -1);
    }

    public void makeEmpty() {
        top = -1;
    }

    public void print() {
        int i = top;
        while (i != -1) {
            System.out.println(array[i--]);
        }
    }
}

/**
 * Class: StackDyanamicArray
 * Stack implementation using Linked List
 */
class StackLinkedList implements IStack {

    Node top = null;

    public void push(String c) {
        Node node = new Node(c);
        node.next = top;
        top = node;
    }

    public String pop() {
        Node tmp = top;
        top = top.next;
        return tmp.data;
    }

    public String peek() {
        if (top != null) {
            return top.data;
        } else {
            return " ";
        }
    }

    public boolean isEmpty() {
        return (top == null);
    }

    public void print() {
        Node i = top;
        System.out.println();
        while (i != null) {
            System.out.println(i.data);
            i = i.next;
        }
    }

    public void makeEmpty() {
        top = null;
    }
}


/**
 * Class: Node
 * Node for every item in linked list
 */
class Node {

    public String data;
    public Node next;

    public Node(String data) {
        this.data = data;
        this.next = null;
    }
}

/**
 * Interface: IStack
 * Defines the methods to be implemented in the class
 */
interface IStack {

    public void push(String c);
    public String pop();
    public String peek();
    public boolean isEmpty();
    public void print();
    public void makeEmpty();
}

/**
 * Class: Logger
 * Logs the messages in a text file
 */
class Logger {

    BufferedWriter fileOutputBuffer;

    public Logger(String filename) throws IOException {
        fileOutputBuffer = new BufferedWriter(new FileWriter(filename));
    }

    public void log(String message) throws IOException {
        fileOutputBuffer.write(message);
        fileOutputBuffer.newLine();
    }

    public void terminate() throws IOException {
        fileOutputBuffer.flush();
        fileOutputBuffer.close();
    }
}
