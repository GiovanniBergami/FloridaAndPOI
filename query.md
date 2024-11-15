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

// Parametro dell'indice di fascia di età, impostato dall'utente
const ageIndex = 2;  // Ad esempio, 2 indica la terza fascia di età

db.POIs.aggregate([
// Proiezione per estrarre la città e il valore `reviews_count_for_age` all'indice specificato
{
$project: {
name: 1,
city: 1,
reviews_count_for_age: { $arrayElemAt: ["$reviews_count_for_age", ageIndex] }
}
},

    // Raggruppamento per città, selezionando il POI con il massimo `reviews_count_for_age` per l'indice specificato
    {
        $sort: { "reviews_count_for_age": -1 }
    },
    {
        $group: {
            _id: "$city",
            topPOI: { $first: "$name" },
            maxReviewsForAge: { $first: "$reviews_count_for_age" }
        }
    },

    // Ordinamento finale per rendere più leggibile
    {
        $sort: { "_id": 1 }
    }
]);
