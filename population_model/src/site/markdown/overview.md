# Overview

The population is used to generate to generate populations of individuals conforming to the IPerson interface.

The organic approach to the population model is event driven. For everything that occurs within the simulation an event is created on a given day and then placed into a global event queue.

Events are taken from the queue in date order at which point they are handled so that the required operations are perfomred within the population to reflect the occurance of the event.

The way in which the simulation progresses is defined by the types of events which are modelledd and the point in time which they occur which is goverend by statistical distributions.

---

## Events
### Person Events
__BORN__ - Marks the point in time when this individual is to be born into the population. At this point the family type for thier first partnership. Types that can be allocated at this point are:

* __SINGLE__ - Never to have a marraige or cohabitation partnership.
* __COHABITATION__ - First partnership to be purely cohabiatation.
* __COHABITATION_THEN_MARRIAGE__ - First partnership to be begin with cohabitation but lead to marriage.
* __MARRIAGE__ - First partnership to be marriage with no period of prior cohabitation.