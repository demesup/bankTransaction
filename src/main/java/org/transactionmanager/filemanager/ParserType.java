package org.transactionmanager.filemanager;

import lombok.Getter;

@Getter
public enum ParserType {
    YAML("yaml"),
    CSV(".csv"),
    XML(".xml"),
    JSON(".json");
    final String ending;


    ParserType(String ending) {
        this.ending = ending;
    }
}
