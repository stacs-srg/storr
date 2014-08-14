# Overview
---
The population is used to generate to generate populations of individuals conforming to the IPerson interface.

The organic approach to the population model is event driven. For everything that occurs within the simulation an event is created on a given day and then placed into a global event queue.

Events are taken from the queue in date order at which point they are handled so that the required operations are performed within the population to reflect the occurrence of the event.

The way in which the simulation progresses is defined by the types of events which are modeled and the point in time which they occur which is governed by statistical distributions.

---

## Events

---

### Person Event
__BORN__ - Marks the point in time when this individual is to be born into the population. At this point the family type for their first partnership. Types that can be allocated at this point are:

* __SINGLE__ - Never to have a marriage or cohabitation partnership.
* __COHABITATION__ - First partnership to be purely cohabitation.
* __COHABITATION_THEN_MARRIAGE__ - First partnership to be begin with cohabitation but lead to marriage.
* __MARRIAGE__ - First partnership to be marriage with no period of prior cohabitation.

An age at which the person will become eligible to begin the specified partnership will also be assigned and an associated event added to the person's time line and to the global event queue. The created events are typed as shown below:


| Family Type | Event Type | Associated File |
| :---: | :---: | :--- |
| SINGLE | COMING\_OF\_AGE | _COMING\_OF\_AGE_AGE (15)_ |
| COHABITATION | ELIGABLE\_TO\_COHABIT | cohabitation\_age\_for\_{gender}\_distributions\_data.tsv |
| COHABITATION\_THEN | ELIGABLE\_TO | cohabitation\_age\_for\_{gender}\_distributions\_data.tsv |
| _MARRIAGE| _COHABIT\_THEN\_MARRIAGE | cohabitation_to_marriage_time_distributions_data.tsv |
| MARRIAGE | ELIGABLE\_TO\_MARRY | cohabitation\_age\_for\_{gender}\_distributions\_data.tsv |

---

__COMING_OF_AGE__ - At the point of coming of age the person is placed into the gender specific singles queue. Although these persons won't be part of any cohabitation or marriage partnerships they may still be used in affairs and extramarital partnerships.

---

__ELIGABLE_TO_COHABIT__ - At the point of a person becoming eligible to cohabit the person is placed into the gender specific cohabitation queue.

---

__ELIGABLE_TO_COHABIT_THEN_MARRY__ - At the point of a person becoming eligible to cohabit then marry the person is placed into the gender specific cohabitation then marriage queue.

---

__ELIGABLE_TO_MARRY__ - At the point of a person becoming eligible to cohabit the person is placed into the gender specific marriage queue.

---

__DEATH__ - Marks the point of death of the individual at which point they are moved from the living population to the deceased population.

---

### Partnership Events
__BIRTH__ - The point in time at which children of the partnership are born. These offspring will have already been created and initialised at the point where the BIRTH event was created. This can occur in two places, in the case of the first birth in the partnership at the creation of the partnership and for all subsequent births at the point of the previous birth event.

####_Relevant distributions:_
* children_number_of_per_family_distribution_data.tsv
* children_number_of_in_maternity_distributions_data.tsv
* affair_number_of_children_distributions_data.tsv

---

__DIVORCE__ - The point in time at which the partnership is terminated. On handling of this event the change of remarriage is considered and an event is created for each partner as necessary in the form of a new ELIGABLE_TO_MARRY event.

####_Relevant distributions:_
* divorce_remariage_boolean_distributions_data.tsv



---

## Partnership Queues

---

A pair of queues exist which hold the individuals ready to undertake each type of partnership. Following each time iteration in the simulation an attempt is made to pair up as many of the persons in the queues as possible. Details of the creation of the different types of partnership are detailed below.

### Marriage Queues

At the point of marriage a number of decisions are made about the partnership:

1. The inheritance of the male name as the family name.
2. If the partnership will terminate. If so:
    * then an end date is set for the partnership, this is enforced to be before the death of either person.
    * the reason for termination and the instigating person is decided.
        * If adultery then a number (1 or more) of affairs are set up for the non instigating person.
3. The number of children to result from the partnership.
4. The first birth event is created and the children borne from it created and initialised. This includes assignation of:
    * Names
    * Occupation
    * Cause of death
    * Gender
    * Birth and death dates

####_Relevant Queues:_

* Male Marriage Queue
* Female Marriage Queue

####_Relevant distributions:_

* children_number_of_in_marriage_or_cohab_distributions_data.tsv
* divorce_instigated_by_gender_distributions_data.tsv
* divorce_age_for_male_distributions_data.tsv
* divorce_age_for_female_distributions_data.tsv
* children_number_of_in_maternity_distributions_data.tsv
* divorce_reason_male_distributions_data.tsv
* divorce_reason_female_distributions_data.tsv
* affair_number_of_distributions_data.tsv

### Cohabitation Queues

Cohabitation works in much the same way as marriage in terms of the ordering of events, the termination of the partnership is not assigned an instigating party or reason and is not enforced as being before death and in the case of the termination occurring after death the death date is taken as the termination date.

####_Relevant Queues:_

* Male Cohabitation Queue
* Female Cohabitation Queue

####_Relevant distributions:_

* children_number_of_in_marriage_or_cohab_distributions_data.tsv
* children_number_of_in_maternity_distributions_data.tsv
* cohabitation_length_distributions_data.tsv

### Cohabitation then Marriage Queues

Cohabitation then marriage works in much the same way as marriage in terms of the ordering of events, before the determination of termination of the partnership a wedding date is assigned; after which any termination of the partnership cannot occur before.

####_Relevant Queues:_

* Male Cohabitation then Marriage Queue
* Female Cohabitation then Marriage Queue

####_Relevant distributions:_

* children_number_of_in_marriage_or_cohab_distributions_data.tsv
* divorce_instigated_by_gender_distributions_data.tsv
* divorce_age_for_male_distributions_data.tsv
* divorce_age_for_female_distributions_data.tsv
* children_number_of_in_maternity_distributions_data.tsv
* divorce_reason_male_distributions_data.tsv
* divorce_reason_female_distributions_data.tsv
* affair_number_of_distributions_data.tsv
* cohabitation_to_marriage_time_distributions_data.tsv

### Affairs Queues

At the moment affairs are handled in much the same way as marriage in terms of the ordering of events, however the termination of the affair is set to be the day following the birth of the final child borne in the partnership. In the event that it is not possible for a child to be born in the affair then the partnership is terminated immediately.

####_Relevant Queues:_

_In case of affair where both persons are married_

* Male Marital Affairs Queue
* Female Marital Affairs Queue

_In case of affair where the male is married_

* Male Single Affairs Queue
* Female Single Queue

_In case of affair where the female is married_

* Female Single Affairs Queue
* Male Single Queue

####_Relevant distributions:_

* children_number_of_in_maternity_distributions_data.tsv
* affair_number_of_children_distributions_data.tsv

### Single Queues

Currently only used in conjunction with the affairs queues.

####_Relevant Queues:_

* Male Single Queue
* Female Single Queue

####_Relevant distributions:_
