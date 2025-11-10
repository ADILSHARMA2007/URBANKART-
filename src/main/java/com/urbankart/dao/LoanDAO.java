package com.urbankart.dao;

import com.urbankart.model.Loan;
import java.time.LocalDate;
import java.util.List;

public interface LoanDAO {
    boolean createLoan(Loan loan);
    List<Loan> getLoansByProduct(int productId);
    List<Loan> getLoansByUser(int userId);
    boolean isProductAvailable(int productId, LocalDate startDate, LocalDate endDate);
}