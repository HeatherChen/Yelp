Yelp
====

CS224W final project

Stats:
- category distribution + how many categories in total
- Business score distribution, ex. business avg score (total/ per category)
- User avg star distribution
- User review count distribution
- Business review count distribution
- review votes vs. difference between true score and the review score (three lines for funny, useful, cool)
- business score vs. review count correlation (x_axis: score, y_axis: review count box plot) (goal: want to know 
  if a business having more reviews also has a higher score)

User features used for credibility:
1. avg stars, 1.26
2. review counts, 0.83
3. funny votes, 0.21
4. useful votes, 5.12
5. cool votes, -6.89
6. total votes, -0.35
7. different star from the true star (total), -0.35
8. different star from the true star (abs), 0.18

[ 1.25687047  0.8287024   0.21196656  5.12348454 -6.8873236  -0.35090351
 -0.35062001  0.18453315  0.15627555]

Sections:
- Abstract
- Introduction
- Related Work
- Data
- Dataset Statistics
- User Credibility Score
   1 HITS-Based 
   2 Feature-Based
- Difficulty
- Future Work
