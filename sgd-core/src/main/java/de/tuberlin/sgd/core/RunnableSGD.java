package de.tuberlin.sgd.core;

public class RunnableSGD implements Runnable {

    final private SGD.SGDRegressor sgd = new SGD.SGDRegressor();

    private DVector params;
    private DVectorFrame frame;
    private int[] X_indices;
    private int Y_index;
    private int numSamples;

    public RunnableSGD( DVector params,  DVectorFrame frame,  int[] X_indices,  int Y_index,  int numSamples){

        this.params = params;
        this.frame = frame;
        this.X_indices = X_indices;
        this.Y_index = Y_index;
        this.numSamples = numSamples;

    }

    @Override
    public void run() {
        sgd.fit(this.params, this.frame, this.X_indices, this.Y_index, this.numSamples);
    }


}
