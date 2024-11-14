Query definitive:

OTTENERE LA LISTA DEI POI DI UNA CITTA CON STATISTICHE

QUERY IN JSON:

db.POIs.aggregate([
// Filtro per città
{ $match: { city: "Nome della città" } },

    // Proiezione dei campi con calcoli sui rapporti
    { $project: {
        name: 1,
        city: 1,
        totStars: 1,
        reviews_count: 1,
        
        // Calcolo della media generale delle stelle
        avgStars: { 
            $cond: { 
                if: { $gt: ["$reviews_count", 0] },
                then: { $divide: ["$totStars", "$reviews_count"] },
                else: 0
            }
        },
        
        // Calcolo della media delle stelle per ciascuna fascia di età
        ageGroupAvgStars: {
            $map: {
                input: { $range: [0, { $size: "$stars" }] },
                as: "index",
                in: {
                    $cond: {
                        if: { $gt: [{ $arrayElemAt: ["$reviews_count_for_age", "$$index"] }, 0] },
                        then: {
                            $divide: [
                                { $arrayElemAt: ["$stars", "$$index"] },
                                { $arrayElemAt: ["$reviews_count_for_age", "$$index"] }
                            ]
                        },
                        else: 0
                    }
                }
            }
        }
    }}
])

QUERY IN JAVA: VEDI CODICE

GET BEST POI FOR AGE ACROSS CITIES

const ageIndex = 2;
db.POIs.aggregate([
{
$match: {
[`reviews_count_for_age.${ageIndex}`]: { $gt: 0 }
}
},
{
$group: {
_id: "$city",
maxReviewsCount: { $max: { $arrayElemAt: ["$reviews_count_for_age", ageIndex] } },
poiName: { $first: "$name" },
poiId: { $first: "$_id" }
}
},
{
$project: {
city: "$_id",
poiName: 1,
reviewsCountForAge: "$maxReviewsCount"
}
}
]);
