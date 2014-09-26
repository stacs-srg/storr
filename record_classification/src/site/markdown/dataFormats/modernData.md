# Modern Data File Format

1,"Pneumonia","Old Age",,,"Ischaemic Heart Disease; Heart Failure",1,1,"J189",2,1,"R54",6,1,"I509",,,,,,,,,,,,,,,,,,,"I509","8","F","10","2012"

An overview of the pilot data file format is given here.     
    
Example Records:        

ID|Cause of Death 1|Cause of Death 2|Cause of Death 3|Underlying Cause of Death|Cod Position| Cod Column|ICD10 Code|Cod Position| Cod Column|ICD10 Code|Cod Position| Cod Column|ICD10 Code|Cod Position| Cod Column|ICD10 Code|Cod Position| Cod Column|ICD10 Code|Cod Position| Cod Column|ICD10 Code|Cod Position| Cod Column|ICD10 Code|Cod Position| Cod Column|ICD10 Code|Underlying ICD10 Code| Age Group?| Sex| Decade?|Year|
1|"Chest Infection","Old Age"|||"Ulcerative Proctitis"|1|1|"J988"|2|1|"R54"|6|1|"K512"|||||||||||||||||||"J988"|"8"|"M"|"10"|"2014"|



    
Notes    

* Causes of death columns and image quality columns can be null.    
* Sex must be 1 or 2    
* Age group must be between 0 and 5    
