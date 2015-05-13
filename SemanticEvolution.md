# Introduction #

Preserving knowledge for future generations is a major reason for collecting all kinds of publications, web pages, etc. in archives. However, ensuring the archival of content is just the first step toward "full" content preservation. It also has to be guaranteed that content can be found and interpreted in the long run.

This type of semantic accessibility of content suffers due to changes in language over time, especially if we consider time frames beyond ten years. Language changes are triggered by various factors including new insights, political and cultural trends, new legal requirements, high-impact events, etc. Due to this terminology development over time, searches with standard information retrieval techniques, using current language or terminology would not be able to find all relevant content created in the past, when other terms were used to express the same sought content. To keep archives semantically accessible it is necessary to develop methods for automatically dealing with terminology evolution.

Word sense discrimination is the first, important step towards automatic detection of language evolution within large, historic document collections. By comparing found word senses over time, important information can be revealed and used to improve understanding and accessibility of a digital archive. Therefore, applying word sense discrimination and evaluating the output will be the first step in our evaluations.

Word sense discrimination is the task of automatically finding the sense classes of terms present in a collection. The output of word sense discrimination is sets of terms describing senses found in the collection. This grouping of terms is derived from clustering and we therefore refer to such an automatically found sense as a _cluster_.


# Processing pipeline #

The processing pipeline depicted below consists of four major steps: pre-processing, natural language processing, creation of co-occurrence graph, and clustering. Each step is performed for each year separately. In the rest of this section, we will describe the processing pipeline in detail.

![http://wiki.liwa-technologies.googlecode.com/hg/SemanticEvolution-pipeline.jpg](http://wiki.liwa-technologies.googlecode.com/hg/SemanticEvolution-pipeline.jpg)

## Pre-Processing ##

The first step towards finding word senses is to prepare the documents in the archive for the subsequent processing. For The Times Archive we need to extract the content from the provided XML documents and to perform an initial cleaning of the data.

## Natural Language Processing ##

This step extracts nouns and noun phrases out of the cleaned text. Therefore, it is first passed to a linguistic processor which uses a part-of-speech tagger to identify nouns. In addition, terms are lemmatized if a lemma can be derived.

## Co-Occurrence Graph Creation ##

After the natural language processing step, a _co-occurrence graph_ is created. Typically the sliding window method is used for creating the graph but our initial experiments indicated that sliding windows in conjunction with the curvature clustering algorithm provide clusters corresponding to events rather than word senses. Therefore we use instead the following language oriented approach, which has been described in deliverable D6.5.

Using the nouns and noun phrases corresponding to the particular year, the collection is searched for lists of nouns and noun phrases. Terms from the dictionary, that are found in the text separated by an “and”, an “or” or a comma, are considered to be co-occurring. For example if in the sequence “…cities such as Paris, New York and Berlin...” the terms "_Paris_", "_New York_" and "_Berlin_" are all co-occurring in the graph.

## Graph Clustering ##

The clustering step is the core step of word sense discrimination and takes place once the co-occurrence graph is created.  The curvature clustering algorithm by Dorow is used to cluster the graph. The algorithm calculates the clustering coefficient, also called the curvature value, for each node by counting the number of triangles that the node is involved in.


# Integration into the LiWA Architecture #

The Terminology Evolution Module is subdivided into terminology extraction and tracing of terminology evolution. Both sub-modules are integrated via UIMA pipelines as presented in the Figure below. The terminology extraction sub-module is automatically triggered when a crawl or a partial crawl is finished. The terminology evolution sub-module is manually triggered by the archive curator based on the crawl statistics gathered during terminology extraction.

![http://wiki.liwa-technologies.googlecode.com/hg/SemanticEvolution-architecture.jpg](http://wiki.liwa-technologies.googlecode.com/hg/SemanticEvolution-architecture.jpg)

## Terminology Extraction Pipeline ##

The WARC Collection Reader (WARC Extraction) extracts the text and time metadata for each site archived in the input crawl. The POS (Part Of Speech) Tagger is an aggregate analysis engine from Dextract . It consists of a tokenizer, a language independent part of speech tagger and lemmatizer (TreeTagger). In the Term Extraction sub-module, we read the annotated sites, extract the lemmas and the different occurring parts of speech that were identified for the archived sites. After that, we index the terms in an database (MySQL) index (see below). In the Co-occurrence Analysis we extract lemma or noun co-occurrence matrices for the indexed crawl from the database index.

![http://wiki.liwa-technologies.googlecode.com/hg/SemanticEvolution-extraction.jpg](http://wiki.liwa-technologies.googlecode.com/hg/SemanticEvolution-extraction.jpg)

## Terminology Evolution Pipeline (under development) ##

After extracting the co-occurrence matrices for lemmas for different crawls captured at different moments in time, for the different time intervals, we cluster the lemmas with a curvature clustering algorithm. The clusters from different time intervals are then analyzed and compared in order to detect term evolution.

# Download and Installation #

## LiWA Curvature Clustering Module ##
The LiWA Curvature Clustering Module is a perl module for word sense discrimination developed by the Leibniz Universität Hannover, L3S Research Center (http://www.L3S.de).

This module uses the curvature clustering algorithm by Dorow, Eckmann and Sergi to cluster a co-occurrence graph. The algorithm calculates the clustering coefficient, also called curvature value, of each node by counting the number of triangles that the node is involved in. The triangles, representing the interconnectedness of the node's neighbors, are normalized by the total number of possible triangles. The curvature value is a value between 0 and 1.

After computing the curvature values, the algorithm removes nodes with a curvature value below a certain curvature threshold. The low curvature nodes represent ambiguous nodes that are likely to connect parts of the graph that would otherwise not be connected. Once these nodes are removed, the remaining graph falls apart into connected components. The connected components, from now on referred to as clusters, are considered to be candidate word senses. In the final step each cluster is enriched with the nearest neighbors of its members. This way the clusters capture also the ambiguous terms and the algorithm is shown to handle both ambiguity as well as polysemy.

This module can be obtained in the [download section](http://code.google.com/p/liwa-technologies/downloads/detail?name=LiwaCurvatureModule.zip&can=2&q=).

## Semantic Analyser ##
The Semantic Analyser consists of two main classes:
  * CPESetup.ARCCollectionParserCPM is used to parse a collection of ARC archives. It reads all the files of text/html mimetype from archives and it extracts all the nouns appearing around conjunctions in the text. It then  stores the lemmas of the nouns plus location information in a MySQL database. It takes as input either the URL where the archives are listed, or the folder with the archives and the number of archives to be processed.
  * graphs.CreateGraphs is used to extract lemma co-occurrence matrixes for different time intervals from
> the database.

This module can be obtained in the [download section](http://code.google.com/p/liwa-technologies/downloads/detail?name=SemanticAnalyser-1.0.zip&can=2&q=).