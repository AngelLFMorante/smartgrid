package com.smartgrid.translator;

import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.djl.translate.Batchifier;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;

public class LSTMTranslator implements Translator<NDArray, NDArray> {

    @Override
    public NDList processInput(TranslatorContext ctx, NDArray input) {
        return new NDList(input);
    }

    @Override
    public NDArray processOutput(TranslatorContext ctx, NDList list) {
        return list.singletonOrThrow();
    }

    @Override
    public Batchifier getBatchifier() {
        return null; // No batching
    }
}
