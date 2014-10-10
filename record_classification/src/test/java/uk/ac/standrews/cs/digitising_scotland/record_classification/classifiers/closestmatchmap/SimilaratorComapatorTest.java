package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.closestmatchmap;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

public class SimilaratorComapatorTest {

    Similaritor<String> s;
    SimilarityMetric<String> metric;
    SimilarityMetricFromSimmetricFactory factory;

    @Before
    public void setUp() throws Exception {

        factory = new SimilarityMetricFromSimmetricFactory();
        metric = factory.create(new Levenshtein());
        s = new Similaritor<>(metric);

    }

    @Test
    public void testShortStrings() {

        String string = "foo";
        String o1 = "foo";
        String o2 = "bar";

        Comparator<String> c = s.getComparator(string);

        testAllVariationsShort(o1, o2, c);

    }

    @Test
    public void testLongStrings() {

        String string = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
        String o1 = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
        String o2 = "Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";

        Comparator<String> c = s.getComparator(string);
        testAllVariationsLong(o1, o2, c);

    }

    @Test
    public void testEmptyKeyString() {

        metric = factory.create(new JaccardSimilarity());
        s = new Similaritor<>(metric);
        String string = "";
        String o1 = "foo";
        String o2 = "bar";

        Comparator<String> c = s.getComparator(string);

        testAllVariationsEmptyKey(o1, o2, c);

    }

    private void testAllVariationsEmptyKey(final String o1, final String o2, Comparator<String> c) {

        Assert.assertEquals(0, c.compare(o1, o2));
        Assert.assertEquals(0, c.compare(o2, o1));
        Assert.assertEquals(0, c.compare(o1, o1));
        Assert.assertEquals(0, c.compare(o2, o2));
        Assert.assertEquals(0, c.compare("", o2));
        Assert.assertEquals(0, c.compare("", o1));
        Assert.assertEquals(0, c.compare(o1, ""));
        Assert.assertEquals(0, c.compare(o2, ""));
        Assert.assertEquals(0, c.compare("", ""));
    }

    private void testAllVariationsShort(final String o1, final String o2, Comparator<String> c) {

        Assert.assertEquals(-1, c.compare(o1, o2));
        Assert.assertEquals(1, c.compare(o2, o1));
        Assert.assertEquals(0, c.compare(o1, o1));
        Assert.assertEquals(0, c.compare(o2, o2));
        Assert.assertEquals(0, c.compare("", o2));
        Assert.assertEquals(1, c.compare("", o1));
        Assert.assertEquals(-1, c.compare(o1, ""));
        Assert.assertEquals(0, c.compare(o2, ""));
        Assert.assertEquals(0, c.compare("", ""));
    }

    private void testAllVariationsLong(final String o1, final String o2, Comparator<String> c) {

        Assert.assertEquals(-1, c.compare(o1, o2));
        Assert.assertEquals(1, c.compare(o2, o1));
        Assert.assertEquals(0, c.compare(o1, o1));
        Assert.assertEquals(0, c.compare(o2, o2));
        Assert.assertEquals(1, c.compare("", o2));
        Assert.assertEquals(1, c.compare("", o1));
        Assert.assertEquals(-1, c.compare(o1, ""));
        Assert.assertEquals(-1, c.compare(o2, ""));
        Assert.assertEquals(0, c.compare("", ""));
    }

    public void testSort() {

        metric = factory.create(new JaccardSimilarity());

        Map<String, String> map = generateMap();
        String key = "arrhythmia";

        ClosestMatchMap<String, String> cmm = new ClosestMatchMap<>(metric, map);
        cmm.getClosestKey(key);

    }

    private Map<String, String> generateMap() {

        Map<String, String> map = new HashMap<String, String>();
        String commaList = "systemic sclerosis, , necrotic liver metastases, old age 99 years, type i diabetes mellitus, kyphosis, gall bladder adenocarcinoma, head trauma, congestive cardiac dysfunction, ischaemic lower limb, metastatic squamous lung cancer, poorly controlled diabetes mellitus, ischaemic cardiomyopathy, general frailty, intra abdominal pathology, natural causes consistent with age, massive subarachnoid haemorrhage, squamous cell carcinoma of ear, gastrointestinal obstruction, cachexia, primary intracerebral haemorrhage, metastatic rectosigmoid tumour, pleural mesothelioma, pulmonary stenosis, perforated peptic ulcer disease, cerebellar infarction, metastatic carcinoma of unknown primary, cerebral metastases, chronic pyometra, adenocarcinoma of oesophagus, general immobility, metastatic neuroblastoma, further haemorrhage, presumed myocardial infarction, vascular disease, cancer of ovary, ischaemic cardiac failure, extreme age, subacute bowel obstruction, type i diabetes, post operative ileus, autoimmune haemolytic anaemia, lung metastasis, systemic sepsis, metastatic sigmoid carcinoma, generalised acute peritonitis, systemic vasculitis, left cerebral infarction carotid territory, recent surgery, hepatic metastases, cancer of colon, probable intracerebral vascular anomaly, cerebral oedema, boat collision with pier, hepatorenal failure, rhabdomyosarcoma of retroperitineum, amyloidosis, carcinoid syndrome, brain metastases, chronic kidney disease on dialysis, septic shock secondary to community acquired pneumonia, pedestrian hit by a car, left renal mass, hypoplastic left heart syndrome, metastatic carcinoma oesophagus, left hemisphere stroke, end stage huntington's disease, squamous cell carcinoma of oesophagus, adenocarcinoma transverse colon, severe pancreatitis, cardiovascular accident, perioperative myocardial infarction, acute cor pulmonale deterioration, dysphagia secondary to cerebral palsy, multiple systems atrophy, anteriolateral st elevation myocardial infarction, basilar artery cerebrovascular event, dementia type unknown, carcinoma of oropharynx, abdominal sarcoma, stage iv sarcoidosis, perforated acute cholecystitis, metastatic primary of unknown origin, portal hypertension, dementia dementia lewy body, sigmoid volvulus with subtotal colectomy, haemopericardium, extensive metastatic disease unknown primary, multiple strokes, metastatic basaloid squamous cell carcinoma of tonsil, cervical spine fracture, paralytic ileus, metastatic squamous cell carcinoma primary unknown, metastatic colorectal cancer, biliary sepsis, metastatic carcinoma of of head pancreas, aspiration pneumonitis, pelvic abscess, bladder cancer adenocarcinoma, haemopneumothorax, epistaxis, sclerosing cholangitis, poor mobility and general frailty, necrosis of right foot, vascular dementia, haemorrhagic stroke left side, mycosis fungoides t cell lymphoma, rectosigmoid carcinoma, carcinoma of distal oesophagus, subpleural fibrosis, left basal pneumonia, maternal chorioamnionitis, metastatic transitional cell carcinoma of bladder, staphylococcal aureus bacteraemia, metastatic oesophageal cancer, addisons disease, no anatomical cause identified decomposed, diverticular stricture, abdominal wall abscess, advanced chronic obstructive pulmonary disease, metastatic malignant prostatic cancer, metastatic choroid plexus carcinoma, impaired swallow, decompensated non alcoholic fatty liver disease, non alcoholic steatohepatitis, parkinsons syndrome, chronic leukaemia, malignant neoplasm of oropharynx, epiglottal cancer, hepatitis, bladder cancer, fractured left neck of femur, oesophagogastric junction adenocarcinoma, previous myocardial infarction, lung metastases, carcinoma of unknown primary, metastatic colon cancer adenocarcinoma, strangulated inguinal hernia, transient ischaemic attack, squamous cell cancer of the lung, high grade non hodgkin's lymphoma, diverticular abscess, carcinomatosis unknown primary, coronary artery thrombosis, metastatic pulmonary carcinoma, deep venous thrombosis, critical ischaemia of lower limbs, fast atrial fibrillation";
        String[] arr = commaList.split(",");
        for (String string : arr) {
            map.put(string, string);
        }
        System.out.println(arr.length);
        System.out.println(map.keySet());
        return map;
    }
}
