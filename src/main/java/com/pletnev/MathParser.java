package com.pletnev;


import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;

public class MathParser {


    public TokensBuffer tokens;
    private HashMap<String, Function> functionMap;

    private void tokensParsing(String expText) {
        tokens=new TokensBuffer();
        int pos = 0;
        while (pos< expText.length()) {
            char c = expText.charAt(pos);
            switch (c) {
                case '(':
                    tokens.add(new Token(TokenType.LEFT_BRACKET, c));
                    pos++;
                    continue;
                case ')':
                    tokens.add(new Token(TokenType.RIGHT_BRACKET, c));
                    pos++;
                    continue;
                case '+':
                    tokens.add(new Token(TokenType.PLUS, c));
                    pos++;
                    continue;
                case '-':
                    tokens.add(new Token(TokenType.MINUS, c));
                    pos++;
                    continue;
                case '*':
                    tokens.add(new Token(TokenType.MUL, c));
                    pos++;
                    continue;
                case '/':
                    tokens.add(new Token(TokenType.DIV, c));
                    pos++;
                    continue;
                case ',':
                    tokens.add(new Token(TokenType.COMMA, c));
                    pos++;
                    continue;
                default:
                    if (Character.isDigit(c)) {
                        StringBuilder sb = new StringBuilder();
                        do {
                            sb.append(c);
                            pos++;
                            if (pos >= expText.length()) {
                                break;
                            }
                            c = expText.charAt(pos);
                        } while (Character.isDigit(c));
                        tokens.add(new Token(TokenType.NUMBER, sb.toString()));
                    } else {
                        if (c != ' ') {
                            if (Character.isLetter(c)) {
                                StringBuilder sb = new StringBuilder();
                                do {
                                    sb.append(c);
                                    pos++;
                                    if (pos >= expText.length()) {
                                        break;
                                    }
                                    c = expText.charAt(pos);
                                } while (Character.isLetter(c));

                                if (functionMap.containsKey(sb.toString())) {
                                    tokens.add(new Token(TokenType.FUNCTION, sb.toString()));
                                } else if(sb.length()==1) {
                                    tokens.add(new Token(TokenType.VARIABLE, sb.toString()));
                                }
                                else{
                                    throw new RuntimeException("Unexpected character: " + c);
                                }
                            }
                        } else {
                            pos++;
                        }
                    }
            }
        }
        tokens.add(new Token(TokenType.EOF, ""));
    }

    private void varParsing()
    {
        Scanner in = new Scanner(System.in);
        for(int i=0;i<tokens.size();i++)
        {
            Token curr_token = new Token();
            curr_token=tokens.get(i);
            if(curr_token.getType()==TokenType.VARIABLE)
            {
                System.out.println("Enter the "+tokens.get(i).getValue());
                double value = in.nextDouble();
                for(int j=i+1;j<tokens.size();j++)
                {
                    if(curr_token.getValue().equals(tokens.get(j).getValue()))
                    {
                        tokens.set(j,(new Token(TokenType.NUMBER,Double.toString(value))));
                    }
                }
                tokens.set(i,(new Token(TokenType.NUMBER,Double.toString(value))));
            }
        }
    }

    private void getFunctionMap() {
        functionMap = new HashMap<String, Function>();
        functionMap.put("pow", args -> {
            if (args.size() != 2) {
                throw new RuntimeException("Wrong argument count for function pow: " + args.size());
            }
            return Math.pow(args.get(0), args.get(1));
        });
        functionMap.put("cos", args -> {
            if (args.size() != 1) {
                throw new RuntimeException("Wrong argument count for function pow: " + args.size());
            }
            return Math.cos(args.get(0));
        });
        functionMap.put("sin", args -> {
            if (args.size() != 1) {
                throw new RuntimeException("Wrong argument count for function pow: " + args.size());
            }
            return Math.sin(args.get(0));
        });
        functionMap.put("tg", args -> {
            if (args.size() != 1) {
                throw new RuntimeException("Wrong argument count for function pow: " + args.size());
            }
            return Math.tan(args.get(0));
        });
        functionMap.put("ctg", args -> {
            if (args.size() != 1) {
                throw new RuntimeException("Wrong argument count for function pow: " + args.size());
            }
            return 1/Math.tan(args.get(0));
        });
        functionMap.put("log", args -> {
            if (args.size() != 1) {
                throw new RuntimeException("Wrong argument count for function pow: " + args.size());
            }
            return Math.log(args.get(0));
        });
    }

    private double expr() {
        Token token = tokens.next();
        if (token.getType() == TokenType.EOF) {
            return 0;
        } else {
            tokens.back();
            return plusMinus();
        }
    }

    private double plusMinus() {
        double value = multDiv();
        while (true) {
            Token token = tokens.next();
            switch (token.getType()) {
                case PLUS:
                    value += multDiv();
                    break;
                case MINUS:
                    value -= multDiv();
                    break;
                case EOF:
                case RIGHT_BRACKET:
                case COMMA:
                    tokens.back();
                    return value;
                default:
                    throw new RuntimeException("Unexpected token: " + token.getValue()
                            + " at position: " + tokens.getPos());
            }
        }
    }

    private double multDiv() {
        double value = factor();
        while (true) {
            Token token = tokens.next();
            switch (token.getType()) {
                case MUL:
                    value *= factor();
                    break;
                case DIV:
                    value /= factor();
                    break;
                case EOF:
                case RIGHT_BRACKET:
                case COMMA:
                case PLUS:
                case MINUS:
                    tokens.back();
                    return value;
                default:
                    throw new RuntimeException("Unexpected token: " + token.getValue()
                            + " at position: " + tokens.getPos());
            }
        }
    }

    private double factor() {
        Token token = tokens.next();
        switch (token.getType()) {
            case FUNCTION:
                tokens.back();
                return func();
            case MINUS:
                double value = factor();
                return -value;
            case NUMBER:
                return Double.parseDouble(token.getValue());
            case LEFT_BRACKET:
                value = plusMinus();
                token = tokens.next();
                if (token.getType() != TokenType.RIGHT_BRACKET) {
                    throw new RuntimeException("Unexpected token: " + token.getValue()
                            + " at position: " + tokens.getPos());
                }
                return value;
            default:
                throw new RuntimeException("Unexpected token: " + token.getValue()
                        + " at position: " + tokens.getPos());
        }
    }

    private double func() {
        String name = tokens.next().getValue();
        Token token = tokens.next();

        if (token.getType() != TokenType.LEFT_BRACKET) {
            throw new RuntimeException("Wrong function call syntax at " + token.getValue());
        }

        ArrayList<Double> args = new ArrayList<Double>();

        token = tokens.next();
        if (token.getType() != TokenType.RIGHT_BRACKET) {
            tokens.back();
            do {
                args.add( expr());
                token = tokens.next();

                if (token.getType() != TokenType.COMMA && token.getType() != TokenType.RIGHT_BRACKET) {
                    throw new RuntimeException("Wrong function call syntax at " + token.getValue());
                }

            } while (token.getType() == TokenType.COMMA);
        }
        return functionMap.get(name).apply(args);
    }

    public double calculation(String expression)
    {
        getFunctionMap();
        tokensParsing(expression);
        varParsing();
        return expr();
    }
}
