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


query 3:

const cityName = "Pasadena"; // Nome della città specifica

db.POIs.aggregate([
// Filtro per città
{
$match: { city: cityName }
},
// Proiezione per calcolare il rapporto stelle/recensioni per ciascuna fascia di età
{
$project: {
name: 1,
city: 1,
ageGroupRatios: {
$map: {
input: { $range: [0, { $size: "$stars" }] },
as: "index",
in: {
$cond: [
{ $gt: [{ $arrayElemAt: ["$reviews_count_for_age", "$$index"] }, 0] },
{
$divide: [
{ $arrayElemAt: ["$stars", "$$index"] },
{ $arrayElemAt: ["$reviews_count_for_age", "$$index"] }
]
},
0
]
}
}
}
}
},
// Scompone ogni POI in più documenti, uno per fascia di età
{
$unwind: {
path: "$ageGroupRatios",
includeArrayIndex: "ageIndex"
}
},
// Raggruppa per città e indice di età, trovando il POI con il rapporto più alto
{
$sort: { ageGroupRatios: -1 }
},
{
$group: {
_id: { city: "$city", ageIndex: "$ageIndex" },
topPOI: { $first: "$name" },
maxRatio: { $first: "$ageGroupRatios" }
}
},
// Ordina il risultato per città e fascia di età
{
$sort: { "_id.city": 1, "_id.ageIndex": 1 }
}
]);


query 4:
db.POIs.aggregate([

{
$group: {
_id: "$city",
totalStars:{$sum : "$totStars"},
totalReviews:{$sum : "$reviews_count"},
values : {
$push: "$stars"
},
reviews_agg:{
$push:"$reviews_count_for_age"}
}
},
{
$project: {
result: {
$reduce: {
input: { $slice: [ "$values", 1, { $size: "$values" } ] },
initialValue: { $arrayElemAt: [ "$values", 0 ] },
in: {
$map: {
input: { $range: [ 0, { $size: "$$this" } ] },
as: "index",
in: {
$add: [
{ $arrayElemAt: [ "$$this", "$$index" ]  },
{ $arrayElemAt: [ "$$value", "$$index" ]  }
]
}
}
}
}
},
result2: {

                $reduce: {
                    input: { $slice: [ "$reviews_agg", 1, { $size: "$reviews_agg" } ] },
                    initialValue: { $arrayElemAt: [ "$reviews_agg", 0 ] },
                    in: {
                        $map: {
                            input: { $range: [ 0, { $size: "$$this" } ] },
                            as: "index",
                            in: {
                                $add: [
                                    { $arrayElemAt: [ "$$this", "$$index" ]  },
                                    { $arrayElemAt: [ "$$value", "$$index" ]  }
                                ]
                            }
                        }
                    }
                }}
,
totalStars : 1,
totalReviews: 1
}
}
])