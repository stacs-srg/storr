# Modern Data File Format

An overview of the modern data file format is given here.     
    
Example Records:    

ID	|Cause of Death 1	|Cause of Death 2	|Cause of Death 3	|Underlying Cause of Death	  |Cod Column	| Cod Position	|ICD10 Code	|Cod Column	| Cod Position	|ICD10 Code	|Cod Column	| Cod Position	|ICD10 Code	|Cod Column	| Cod Position	|ICD10 Code	|Cod Column	| Cod Position		|ICD10 Code	|Cod Column	| Cod Position		|ICD10 Code	|Cod Column	| Cod Position		|ICD10 Code	|Cod Column	| Cod Position	|Underlying ICD10 Code|Age Group	|Sex| Decade|Year|
----|-------------------|-------------------|-------------------|:---------------------------:|---------------|---------------|-----------|---------------|---------------|-----------|---------------|---------------|-----------|---------------|-----------|-----------|---------------|---------------|-----------|---------------|---------------|-----------|---------------|---------------|-----------|---------------|---------------|---------------------|-------------|---|-------|----|
1	|"Pneumonia "       |  "Old Age"        |		   		    |"Ischaemic Heart Disease; Heart Failure"| 1  |1	        |"J189"      | 2 			|1				|"R54"		|6				|1				| "I509"	|				|			|			|				|				|			|				| 				|			|				|				|			|				|				|I509 				  |8			| M	|10		|2014|
250	|Head Injury        |Traffic Accident - Driver  |		    |                              | 1            |1	        |"S099"      | 2 			|1				|"V435"		|				|				|  	|				|			|			|				|				|			|				| 				|			|				|				|			|				|				|V435 				  |8			| M	|10		|2014|

    
Notes    

* Empty columns can be left blank     
* 
* Sex must be 1 or 2    
* Age group must be between 0 and 5    
