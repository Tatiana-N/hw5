package com.github.javarar.lucky.ticket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class LuckyTicket extends RecursiveTask<List<List<Integer>>> {
    final int seqThreshold;
    private final int start;
    private final int end;
    private final Integer size;
    
    public LuckyTicket(Integer start, Integer end, Integer size) {
        this.seqThreshold = (10 * size) / 2;
        this.start = start;
        this.end = end;
        this.size = size;
    }
    
    public static void main(String[] args) {
        System.out.println(luckyTicketProbability(10));
    }
    
    public static int luckyTicketProbability(int serialNumberLength) {
        if (serialNumberLength % 2 != 0){
            throw new IllegalArgumentException("Странные у вас билеты, считайте - все счастливые");
        }
        long start = System.currentTimeMillis();
        int numberOfTickets = (int) Math.pow(10, serialNumberLength);
        LuckyTicket luckyTicket = new LuckyTicket(0, numberOfTickets, serialNumberLength);
        luckyTicket.fork();
        List<List<Integer>> join = luckyTicket.join();
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(numberOfTickets);
        return join.size();
    }
    
    @Override
    protected List<List<Integer>> compute() {
        if ((end - start) < seqThreshold) {
            List<List<Integer>> list = new ArrayList<>();
            for (int i = start; i < end; i++) {
                int firstHalf = sumDigits(i / seqThreshold);
                int secondHalf = sumDigits(i % seqThreshold);
                if (firstHalf == secondHalf) {
                    list.add(getList(i, size));
                }
            }
            return list;
        } else {
            LuckyTicket subLuckyTicket1 = new LuckyTicket( start, start + (end - start) / 2, size);
            LuckyTicket subLuckyTicket2 = new LuckyTicket(start + (end - start) / 2, end, size);
            List<List<Integer>> result = new ArrayList<>();
            subLuckyTicket1.fork();
            subLuckyTicket2.fork();
            result.addAll(subLuckyTicket1.join());
            result.addAll(subLuckyTicket2.join());
            return result;
        }
    }
    
    private List<Integer> getList(int i, int serialNumberLength) {
        String s = String.format("%0" + serialNumberLength + "d", i);
        return Arrays.stream(s.split("")).map(Integer::valueOf).collect(Collectors.toList());
    }
    
    private int sumDigits(int i) {
        int sum = 0;
        while (i > 0) {
            sum += i % 10;
            i /= 10;
        }
        return sum;
    }
}