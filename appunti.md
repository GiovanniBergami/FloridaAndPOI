Large Scale project
Query utili con mongodb:
fare il doc linking:

for (const myCity of db.cities.find()) {
// Trova tutti i punti di interesse relativi alla città corrente
const Punti = db.POIs.find({ city: myCity.nome }).toArray(); // Converte il cursor in un array

    // Estrai solo gli `_id` dei punti di interesse
    const POI_ids = Punti.map(punto => punto._id);

    // Aggiungi l'array di riferimenti `_id` alla città
    db.cities.updateOne(
        { _id: myCity._id },
        { $set: { "POI_ids": POI_ids } }
    );
}

fare una aggregazione su una variabile:

db.cities.aggregate(
{
$group : { _id: null , sommaq : { $sum : "$POI_count"}}
})

for ( const myUser of db.users.find()){
    for ( const f of myUser.friends){
        db.users.updateOne(
            {user_id = f.user_id},
            {$addToSet: {friends : myUser.user_id}}
        )

    }
}

