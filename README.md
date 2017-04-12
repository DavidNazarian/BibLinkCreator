## BibLinkCreator

The BibLinkCreator (Bibliographic Link Creator), is a Java library capable of creating links between RDF datasets relevant to literature. It extracts, preprocesses and validates data from pairs of RDF repositories, and links them based on a combination of key-based and similarity-based approaches.

Currently, the keys used to link data are divided into two categories (A and B). Category-A keys (such as ISBN, DOI, etc.) are those that can uniquely identify a literature item, while category-B keys (such as ISSN or the title of serial publication) are those that identify groups of literature items.

The category-A identifiers that can be used are the following:

Identifier | Description
---------- | -----------
arXiv ID   | [arXiv.org identifier](https://arxiv.org/help/arxiv_identifier)
DOI        | [Digital Object Identifier](http://www.doi.org/doi_handbook/1_Introduction.html)
ISBN       | [International Standard Book Number](https://www.isbn-international.org/sites/default/files/ISBN%20Manual%202012%20-corr.pdf)
LCCN       | [Library of Congress Control Number](https://www.loc.gov/marc/lccn_structure.html)
OCLC       | [Online Computer Library Center Control Number](https://www.oclc.org/support/services/batchload/controlnumber.en.html)
PMID       | [PubMed identifier](https://support.ncbi.nlm.nih.gov/link/portal/28045/28049/Article/508/What-is-a-unique-record-identifier)

The category-B identifiers are the following:

Identifier   | Description
------------ | -----------
ISSN         | [International Standard Serial Number](http://www.issn.org/wp-content/uploads/2013/09/ISSNManual_ENG2015_23-01-2015.pdf)
Journal title | The title of a serial publication

The input RDF datasets can either be downloaded from bibliographic sources or can be created by the library by collecting data from various bibliographic APIs.

In order to link category-A data, it matches same identifiers (from repository pairs) and then examines the similarity of the literature titles based on a string similarity measure (such as Jaccard, Dice, Overlap or Cosine) that can be parameterized for a certain threshold. Optionally it can also examine the similarity of the publication years for a maximum absolute difference.

The category-B data are linked with stricter rules. It first matches same identifiers and publication years and then examines the similarity of the literature titles based on a string similarity measure.

## Table of Contents

* [Version](/README.md#version)
* [Prerequisites](/README.md#prerequisites)
* [Usage](/README.md#usage)
* [Sample SPARQL queries](/README.md#sample-sparql-queries)
  * [DBpedia bibliographic references](/README.md#queries-for-the-dbpedia-bibliographic-references-rdf-dataset)
  * [DBLP](/README.md#queries-for-the-dblp-rdf-dataset)
  * [Springer](/README.md#queries-for-the-springer-rdf-dataset)
  * [Biblioteca Nacional de España (BNE)](/README.md#queries-for-the-biblioteca-nacional-de-espa%C3%B1a-bne-rdf-dataset)
  * [British National Bibliography (BNB)](/README.md#queries-for-the-british-national-bibliography-bnb-rdf-dataset)
  * [Deutsche Nationalbibliografie (DNB)](/README.md#queries-for-the-deutsche-nationalbibliografie-dnb-rdf-dataset)
* [Acknowledgment](/README.md#acknowledgment)
* [License](/README.md#license)

## Version

This is the initial release of the BibLinkCreator library.

Version: `1.1.1`

## Prerequisites

The library uses the Sesame API in order to communicate with a triplestore. It has been successfully tested with the [Ontotext GraphDB 7 Free edition](http://graphdb.ontotext.com/documentation/7.0/free/).

The hardware needed to optimally use the library depends on the linking task. Some of the parameters that increase the need for  resources are the following:

* The count of the triples contained in the RDF repositories.
* The count and the type of the user defined proprocessing rules.
* The count of the matched identifiers from the pairs of RDF repositories.

## Usage

Let's suppose that we want to create ISBN links between the [DBpedia bibliographic references RDF dataset](http://downloads.dbpedia.org/temporary/citations/enwiki-20160305-citation-data.ttl.bz2) and the [DBLP RDF dataset](http://dblp.l3s.de/dblp.rdf.gz).

In can be done after we:

1. Create a repository in Ontotext GraphDB, call it "DBpediaCitations" and import the [DBpedia references RDF dataset](http://downloads.dbpedia.org/temporary/citations/enwiki-20160305-citation-data.ttl.bz2).
2. Create a second repository, call it "DBLP" and import the [DBLP RDF dataset](http://dblp.l3s.de/dblp.rdf.gz).
3. Create a third repository for the results (preprocessed data and links) and call it "BibLinkCreator".
4. Build the biblinkcreator-1.1.1.jar file and include it in a Java project.
5. Use the following code:
```java
RepositoryInfo dbpediaRepositoryInfo = 
               new RepositoryInfo("http://localhost:7200", "DBpediaCitations", "DBpedia", "", null);
RepositoryInfo dblpRepositoryInfo =
               new RepositoryInfo("http://localhost:7200", "DBLP", "DBLP", "", null);
RepositoryInfo extractionDestRepositoryInfo =
               new RepositoryInfo("http://localhost:7200", "BibLinkCreator", "BibLinkCreator",
                   "http://biblinkcreator", null);
Query query = new Query();
Logger logger = new Logger(Logger.PrintType.All, "c:\\temp\\biblinkcreator_log.txt", false);
String seeAlsoURIString = "http://www.w3.org/2000/01/rdf-schema#seeAlso";
CategoryAToleranceData toleranceCatAData = new CategoryAToleranceData(0.8, 0.9, 1);
StringSimilaritySelector categoryASimilaritySelector = 
                         new StringSimilaritySelector(SimilarityType.Overlap, ShingleType.CHAR, 2);
DataExtractor dataExtractor = 
              new DataExtractor(dbpediaRepositoryInfo, extractionDestRepositoryInfo, null, logger);
int recordCount = dataExtractor.extractData(query.dbpediaISBNQuery, query.dblpISBNQuery,
                  dblpRepositoryInfo, IdentifierType.ISBN, false, null);
dataExtractor.close();

if (recordCount > 0) {
    DataLinker dataLinker = new DataLinker(extractionDestRepositoryInfo, logger);
    int linkCount = dataLinker.linkData(dbpediaRepositoryInfo.getRepositoryName(),
                                        dblpRepositoryInfo.getRepositoryName(),
                                        seeAlsoURIString, false, CategoryAIdentifierType.ISBN,
                                        toleranceCatAData, categoryASimilaritySelector);
    
    if (linkCount > 0) {
        dataLinker.exportLinks(dbpediaRepositoryInfo.getRepositoryName(),
        dblpRepositoryInfo.getRepositoryName(), IdentifierType.ISBN,
        "c:\\temp\\dbpedia_to_dblp_isbn_links.nt");
        dataLinker.close();
    }
}
```

The GraphDB server path used in the example is its default (http://localhost:7200), change it according to your settings.

The `Query` class is not part of the library. Sample extraction queries for a number of RDF datasets are given bellow.

## Sample SPARQL queries

#### Queries for the [DBpedia bibliographic references RDF dataset](http://downloads.dbpedia.org/temporary/citations/enwiki-20160305-citation-data.ttl.bz2)

```sparql
#DBpedia arXiv ID query (category-A)
PREFIX dbp: <http://dbpedia.org/property/>

SELECT *
WHERE {
          ?subject dbp:arxiv ?arxivID .
          ?subject dbp:title ?title .
          OPTIONAL {?subject dbp:year ?year .}
          FILTER NOT EXISTS {?subject dbp:chapter ?chapter}
      }
```

```sparql
#DBpedia DOI query (category-A)
PREFIX dbp: <http://dbpedia.org/property/>

SELECT *
WHERE {
          ?subject dbp:doi ?doi .
          ?subject dbp:title ?title .
          OPTIONAL {?subject dbp:year ?year .}
          FILTER NOT EXISTS {?subject dbp:chapter ?chapter}
      }
```

```sparql
#DBpedia ISBN query (category-A)
PREFIX dbp: <http://dbpedia.org/property/>

SELECT *
WHERE {
          ?subject dbp:isbn ?isbn .
          ?subject dbp:title ?title .
          OPTIONAL {?subject dbp:year ?year .}
          FILTER NOT EXISTS {?subject dbp:chapter ?chapter}
      }
```

```sparql
#DBpedia LCCN query (category-A)
PREFIX dbp: <http://dbpedia.org/property/>

SELECT *
WHERE {
          ?subject dbp:lccn ?lccn .
          ?subject dbp:title ?title .
          OPTIONAL {?subject dbp:year ?year .}
          FILTER NOT EXISTS {?subject dbp:chapter ?chapter}
      }
```

```sparql
#DBpedia OCLC query (category-A)
PREFIX dbp: <http://dbpedia.org/property/>

SELECT *
WHERE {
          ?subject dbp:oclc ?oclc .
          ?subject dbp:title ?title .
          OPTIONAL {?subject dbp:year ?year .}
          FILTER NOT EXISTS {?subject dbp:chapter ?chapter}
      }
```

```sparql
#DBpedia PMID query (category-A)
PREFIX dbp: <http://dbpedia.org/property/>

SELECT *
WHERE {
          ?subject dbp:pmid ?pmid .
          ?subject dbp:title ?title .
          OPTIONAL {?subject dbp:year ?year .}
          FILTER NOT EXISTS {?subject dbp:chapter ?chapter}
      }
```

```sparql
#DBpedia ISSN query (category-B)
PREFIX dbp: <http://dbpedia.org/property/>

SELECT *
WHERE {
          ?subject dbp:issn ?issn .
          ?subject dbp:title ?title .
          ?subject dbp:year ?year .
          FILTER NOT EXISTS {?subject dbp:chapter ?chapter}
      }
```

```sparql
#DBpedia journal title query (category-B)
PREFIX dbp: <http://dbpedia.org/property/>

SELECT *
WHERE {
          ?subject dbp:journal | dbp:series | dbp:periodical | dbp:magazine ?journalTitle .
          ?subject dbp:title ?title .
          ?subject dbp:year ?year .
          FILTER NOT EXISTS {?subject dbp:chapter ?chapter}
      }
```

#### Queries for the [DBLP RDF dataset](http://dblp.l3s.de/dblp.rdf.gz)

```sparql
#DBLP DOI query (category-A)
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX dcterms: <http://purl.org/dc/terms/>

SELECT *
WHERE {
          ?subject dc:identifier ?doi .
          FILTER (strstarts(?doi, \"DOI\"))
          ?subject dc:title ?title .
          OPTIONAL {?subject dcterms:issued ?year .}
      }
```

```sparql
#DBLP ISBN query (category-A)
PREFIX swrc: <http://swrc.ontoware.org/ontology#>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX dcterms: <http://purl.org/dc/terms/>

SELECT *
WHERE {
          ?subject swrc:isbn ?isbn .
          ?subject dc:title ?title .
          OPTIONAL {?subject dcterms:issued ?year .}
      }
```

```sparql
#DBLP journal title query (category-B)
PREFIX swrc: <http://swrc.ontoware.org/ontology#>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX dcterms: <http://purl.org/dc/terms/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

SELECT *
WHERE {
          ?subject swrc:journal ?journal .
          ?journal rdfs:label ?journalTitle .
          ?subject dc:title ?title .
          ?subject dcterms:issued ?year .
      }
```

#### Queries for the [Springer RDF dataset](http://lod.springer.com/data/dumps)

```sparql
#Springer DOI query (category-A)
PREFIX spr-p: <http://lod.springer.com/data/ontology/property/>

SELECT *
WHERE {
          ?subject spr-p:bookDOI ?doi .
          ?subject spr-p:title | spr-p:subtitle ?title .
          OPTIONAL {?subject spr-p:hasConference ?conference .
          ?conference spr-p:confYear ?year .}
      }
```

```sparql
#Springer ISBN query (category-A)
PREFIX spr-p: <http://lod.springer.com/data/ontology/property/>

SELECT *
WHERE {
          ?subject spr-p:ISBN | spr-p:ΕISBN ?isbn .
          ?subject spr-p:title | spr-p:subtitle ?title .
          OPTIONAL {?subject spr-p:hasConference ?conference .
          ?conference spr-p:confYear ?year .}
      }
```

#### Queries for the [Biblioteca Nacional de España (BNE) RDF dataset](http://datos.bne.es/datadumps/bibliograficos.nt.bz2)

```sparql
#BNE ISBN query (category-A)
PREFIX bneonto: <http://datos.bne.es/def/>

SELECT *
WHERE {
          ?subject bneonto:P3013 ?isbn .
          OPTIONAL {?subject bneonto:P3006 ?year .}
          ?subject bneonto:P3002 ?P3002 .
          OPTIONAL {?subject bneonto:P3014 ?P3014} .
          BIND (coalesce(?P3002, \"\") as ?titlePartA)
          BIND (coalesce(?P3014, \"\") as ?titlePartB)
          BIND (concat(str(?titlePartA), \" \",
          str(?titlePartB)) AS ?title)
      }
```

```sparql
#BNE ISSN query (category-B)
PREFIX bneonto: <http://datos.bne.es/def/>

SELECT *
WHERE {
          ?subject bneonto:P3039 ?issn .
          ?subject bneonto:P3006 ?year .
          ?subject bneonto:P3002 ?P3002 .
          OPTIONAL {?subject bneonto:P3014 ?P3014} .
          BIND (coalesce(?P3002, \"\") as ?titlePartA)
          BIND (coalesce(?P3014, \"\") as ?titlePartB)
          BIND (concat(str(?titlePartA), \" \",
          str(?titlePartB)) AS ?title)
      }
```

#### Queries for the [British National Bibliography (BNB) RDF dataset](http://www.bl.uk/bibliographic/download.html)

```sparql
#BNB ISBN query (category-A)
PREFIX bibo: <http://purl.org/ontology/bibo/>
PREFIX dcterms: <http://purl.org/dc/terms/>
PREFIX blt: <http://www.bl.uk/schemas/bibliographic/blterms#>
PREFIX event: <http://purl.org/NET/c4dm/event.owl#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

SELECT *
WHERE {
          ?subject bibo:isbn10 | bibo:isbn13 ?isbn .
          ?subject dcterms:title ?title .
          OPTIONAL {?subject blt:publication |
          blt:publicationStart ?publication .
          ?publication event:time ?time .
          ?time rdfs:label ?year .}
      }
```

```sparql
#BNB ISSN query (category-B)
PREFIX bibo: <http://purl.org/ontology/bibo/>
PREFIX dcterms: <http://purl.org/dc/terms/>
PREFIX blt: <http://www.bl.uk/schemas/bibliographic/blterms#>
PREFIX event: <http://purl.org/NET/c4dm/event.owl#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

SELECT *
WHERE {
          ?subject dcterms:isPartOf ?journal .
          ?journal bibo:issn ?issn .
          ?subject dcterms:title | rdfs:label ?title .
          ?subject blt:publication ?publication .
          ?publication event:time ?time .
          ?time rdfs:label ?year .
      }
```

```sparql
#BNB journal title query (category-B)
PREFIX dcterms: <http://purl.org/dc/terms/>
PREFIX blt: <http://www.bl.uk/schemas/bibliographic/blterms#>
PREFIX event: <http://purl.org/NET/c4dm/event.owl#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

SELECT *
WHERE {
          ?subject dcterms:isPartOf ?journal .
          ?journal rdfs:label ?journalTitle .
          ?subject dcterms:title | rdfs:label ?title .
          ?subject blt:publication ?publication .
          ?publication event:time ?time .
          ?time rdfs:label ?year .
      }
```

#### Queries for the [Deutsche Nationalbibliografie (DNB) RDF dataset](http://datendienst.dnb.de/cgi-bin/mabit.pl?userID=opendata&pass=opendata&cmd=login)

```sparql
#DNB ISBN query (category-A)
PREFIX bibo: <http://purl.org/ontology/bibo/>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX dcterms: <http://purl.org/dc/terms/>

SELECT *
WHERE {
          ?subject bibo:isbn10 | bibo:isbn13 ?isbn .
          ?subject dc:title ?title .
          OPTIONAL {?subject dcterms:issued ?year .}
      }
```

```sparql
#DNB OCLC query (category-A)
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX dcterms: <http://purl.org/dc/terms/>

SELECT *
WHERE {
          ?subject dc:identifier ?oclc .
          FILTER (strstarts(?oclc, \"(OColc)\"))
          ?subject dc:title ?title .
          OPTIONAL {?subject dcterms:issued ?year .}
      }
```

```sparql
#DNB ISSN query (category-B)
PREFIX bibo: <http://purl.org/ontology/bibo/>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX dcterms: <http://purl.org/dc/terms/>

SELECT *
WHERE {
          ?subject dcterms:isPartOf ?journal .
          ?journal bibo:issn ?issn .
          ?subject dc:title ?title .
          ?subject dcterms:issued ?year .
      }
```

```sparql
#DNB journal title query (category-B)
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX dcterms: <http://purl.org/dc/terms/>

SELECT *
WHERE {
          ?subject dcterms:isPartOf ?journal .
          ?journal dc:title ?journalTitle .
          ?subject dc:title ?title .
          ?subject dcterms:issued ?year .
      }
```

## Acknowledgment

This project is the result of the MSc Thesis of David Nazarian
under the supervision of Associate Professor Nick Bassiliades
for the partial fulfillment of the requirements for the degree of the
Master of Science in Informatics and Communications at the Department
of Informatics, Aristotle University of Thessaloniki, Greece.

## License

The BibLinkCreator library is provided under the GNU General Public License v3+.

Copyright (C) 2017 David Nazarian
