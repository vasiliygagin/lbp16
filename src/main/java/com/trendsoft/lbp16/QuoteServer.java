package com.trendsoft.lbp16;

import java.io.*;

public class QuoteServer {
    public static void main(String[] args) throws IOException {
	new QuoteServerThread().start();
    }
}