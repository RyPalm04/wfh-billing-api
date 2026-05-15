package com.palmer.wfhbillingapi.controller;

import com.palmer.wfhbillingapi.dto.StatementRequest;
import com.palmer.wfhbillingapi.model.SavedStatement;
import com.palmer.wfhbillingapi.model.StatementSummary;
import com.palmer.wfhbillingapi.service.SavedStatementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statements")
public class StatementController {

    private final SavedStatementService savedStatementService;

    public StatementController(SavedStatementService savedStatementService) {
        this.savedStatementService = savedStatementService;
    }

    @GetMapping()
    public ResponseEntity<List<StatementSummary>> getStatements(){
        return ResponseEntity.ok(savedStatementService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavedStatement> getStatement(@PathVariable int id){
        return ResponseEntity.ok(savedStatementService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<SavedStatement> addStatement(@RequestBody StatementRequest statementRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStatementService.insertSavedStatement(statementRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedStatement> updateStatement(@PathVariable int id, @RequestBody StatementRequest statementRequest){
        return ResponseEntity.ok(savedStatementService.update(id, statementRequest));
    }

    @GetMapping("/next-control-number")
    public int getNextControlNumber(){
        return savedStatementService.nextControlNumber();
    }
}
