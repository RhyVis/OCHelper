package com.rhynia.ochelper.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.rhynia.ochelper.component.DataProcessor;
import com.rhynia.ochelper.util.LuaScriptFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Rhynia
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class RemoteController {

    private final LuaScriptFactory ls;
    private final DataProcessor dp;

    @RequestMapping(value = "/api/oc", method = RequestMethod.GET)
    public ResponseEntity<String> commandSender() {
        var jsonPackedScript = ls.pullAllQuests();
        return new ResponseEntity<>(jsonPackedScript, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/oc", method = RequestMethod.POST, consumes = "text/plain",
        produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> resultHandler(@RequestBody String rb) {
        dp.readAndProcessResult(rb);
        return new ResponseEntity<>("Received", HttpStatus.OK);
    }

}
