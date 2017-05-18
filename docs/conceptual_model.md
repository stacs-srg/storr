# Conceptual Model

<img src="images/old_man_small.jpg" height="300" alt="Old man of Storr by G.Kirby">

Storr is a NoSQL store which is intended to provide easy storage of aribtrary tuples.
Unlike many NOSQL stores it is not based on a key-value storage model and instead supports the storage of arbitrary collections of tuples.
As such it has features in common with NOSQL, relational and graph databases.

Features of the store include:

* logical structuring via *repositories* and *buckets*
* support for both typed or untyped collections of tuples (via *buckets*)
* support for transactions
* support for object references
* a REST API
* a Java API
* optional structural type equivalence
* ability to store types
* indexing over arbitrary fields of the tuples
* *the ability to provide different storage regimes (under development)*

These features are described below.

A **Storr** database is partitioned into three levels:
the [*Store*](https://quicksilver.host.cs.st-andrews.ac.uk/apidocs/storr/uk/ac/standrews/cs/storr/interfaces/IStore.html) which contains [*repositories*](https://quicksilver.host.cs.st-andrews.ac.uk/apidocs/storr/uk/ac/standrews/cs/storr/interfaces/IRepository.html) which in turn contain [*buckets*](https://quicksilver.host.cs.st-andrews.ac.uk/apidocs/storr/uk/ac/standrews/cs/storr/interfaces/IBucket.html).
The tuples that are stored in a Storr database are stored within buckets

This structure was created to permit structuring without the introduction of too much complexity.
Storr was originally created to store Birth, Death and Marriage records.
In our test system we had two sets of these - one from the isle of Skye and another from Kilmarnock.
To support such a setup we might create two repositories callled *Skye* and *Kilmarnock* each of which would contain three buckets: *Births*, *Deaths* and *Marriages*.
