package com.pletnev;

import java.util.ArrayList;

public class TokensBuffer {
    private ArrayList<Token> tokens;
    private int pos;
    private int size;

    public TokensBuffer()
    {
        tokens=new ArrayList<Token>();
        pos=0;
        size=0;
    }

    public void add(Token token)
    {
        tokens.add(token);
        size++;
    }

    public int size()
    {
        return size;
    }

    public Token get(int pos)
    {
        return tokens.get(pos);
    }

    public void set(int pos,Token token)
    {
        tokens.set(pos,token);
    }

    public Token next()
    {
        return tokens.get(pos++);
    }

    public void back()
    {
        if(pos>0)
        pos--;
    }

    public int getPos()
    {
        return pos;
    }

    public void setPos(int pos) throws Exception
    {
        if(pos>-1&&pos<tokens.size())
            this.pos=pos;
        else
            throw new IndexOutOfBoundsException("Pos is out of bounds");
    }

}
