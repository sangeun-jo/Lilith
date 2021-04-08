package sej.calendar.customcalendar.dao;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class Migration implements RealmMigration{
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        if (oldVersion == 0){
            RealmObjectSchema mMemoSchema = schema.get("Memo");

            mMemoSchema.addField("title", String.class, null);
            mMemoSchema.addField("calendar", String.class, null);
            oldVersion++;
        }
    }
}
