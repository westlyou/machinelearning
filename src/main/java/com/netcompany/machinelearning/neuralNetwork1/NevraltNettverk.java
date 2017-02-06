package com.netcompany.machinelearning.neuralNetwork1;

import com.netcompany.machinelearning.preprocessing.DataHjelper;
import org.apache.commons.lang3.Validate;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.nd4j.linalg.util.FeatureUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Oystein Kvamme Repp
 * Date: 29.01.2017
 * Time: 20.59
 */
public class NevraltNettverk {

    private static Logger LOGG = LoggerFactory.getLogger(NevraltNettverk.class);

    private Integer antallKlasser;
    private Integer batchStorrelse;
    private Integer antallEpoker;

    private MultiLayerNetwork modell;


    public NevraltNettverk(final MultiLayerConfiguration nnKonfigurasjon) {
        LOGG.info("Lager modell...");
        antallEpoker = 1;
        batchStorrelse = 1;
        antallKlasser = DataHjelper.ANTALL_KLASSER;
        modell = new MultiLayerNetwork(nnKonfigurasjon);
        modell.init();
    }

    /**
     * Setter antallet epoker modellen skal trenes. Én epoke vil si ett pass over treningsdataene. 1 by default.
     *
     * @param antallEpoker antall epoker
     */
    void setAntallEpoker(final Integer antallEpoker) {
        this.antallEpoker = antallEpoker;
    }

    void setBatchStorrelse(final Integer batchStorrelse) {
        this.batchStorrelse = batchStorrelse;
    }

    void tren(final double[][] treningsdata, int[] treningsfasit) {
        Validate.isTrue(treningsdata.length == treningsfasit.length,
                "Kan ikke trene modell når antall treningseksempler er ulik antall fasit-klasser!");

        final INDArray treningsMatrise = new NDArray(treningsdata);
        modell.setInputMiniBatchSize(batchStorrelse);

        LOGG.info("Trener...");
        modell.setListeners(new ScoreIterationListener(1));
        modell.setListeners(new Graf(10));
        for (int i = 0; i < antallEpoker; i++) {
            modell.fit(treningsMatrise, treningsfasit);
        }
    }

    void evaluer(final double[][] testdata, final int[] testfasit) {
        LOGG.info("Evaluerer...");
        final Evaluation eval = new Evaluation(antallKlasser);

        final INDArray prediksjoner = modell.output(new NDArray(testdata));

        final INDArray fasitSomOutputMatrise = FeatureUtil.toOutcomeMatrix(testfasit, antallKlasser);

        eval.eval(fasitSomOutputMatrise, prediksjoner);

        LOGG.info(eval.stats());
    }
}
