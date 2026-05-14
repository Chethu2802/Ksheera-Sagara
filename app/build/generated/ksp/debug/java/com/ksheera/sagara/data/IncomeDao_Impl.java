package com.ksheera.sagara.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class IncomeDao_Impl implements IncomeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MilkIncome> __insertionAdapterOfMilkIncome;

  private final EntityDeletionOrUpdateAdapter<MilkIncome> __deletionAdapterOfMilkIncome;

  private final EntityDeletionOrUpdateAdapter<MilkIncome> __updateAdapterOfMilkIncome;

  private final SharedSQLiteStatement __preparedStmtOfClear;

  public IncomeDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMilkIncome = new EntityInsertionAdapter<MilkIncome>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `income` (`id`,`dateMillis`,`cowName`,`liters`,`fatPercent`,`pricePerLiter`,`ownerUsername`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MilkIncome entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDateMillis());
        statement.bindString(3, entity.getCowName());
        statement.bindDouble(4, entity.getLiters());
        statement.bindDouble(5, entity.getFatPercent());
        statement.bindDouble(6, entity.getPricePerLiter());
        if (entity.getOwnerUsername() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getOwnerUsername());
        }
        statement.bindLong(8, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfMilkIncome = new EntityDeletionOrUpdateAdapter<MilkIncome>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `income` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MilkIncome entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfMilkIncome = new EntityDeletionOrUpdateAdapter<MilkIncome>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `income` SET `id` = ?,`dateMillis` = ?,`cowName` = ?,`liters` = ?,`fatPercent` = ?,`pricePerLiter` = ?,`ownerUsername` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MilkIncome entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDateMillis());
        statement.bindString(3, entity.getCowName());
        statement.bindDouble(4, entity.getLiters());
        statement.bindDouble(5, entity.getFatPercent());
        statement.bindDouble(6, entity.getPricePerLiter());
        if (entity.getOwnerUsername() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getOwnerUsername());
        }
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfClear = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM income";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final MilkIncome i, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMilkIncome.insertAndReturnId(i);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final MilkIncome i, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMilkIncome.handle(i);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final MilkIncome i, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMilkIncome.handle(i);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clear(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClear.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClear.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MilkIncome>> all() {
    final String _sql = "SELECT * FROM income ORDER BY dateMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"income"}, new Callable<List<MilkIncome>>() {
      @Override
      @NonNull
      public List<MilkIncome> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "dateMillis");
          final int _cursorIndexOfCowName = CursorUtil.getColumnIndexOrThrow(_cursor, "cowName");
          final int _cursorIndexOfLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "liters");
          final int _cursorIndexOfFatPercent = CursorUtil.getColumnIndexOrThrow(_cursor, "fatPercent");
          final int _cursorIndexOfPricePerLiter = CursorUtil.getColumnIndexOrThrow(_cursor, "pricePerLiter");
          final int _cursorIndexOfOwnerUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "ownerUsername");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<MilkIncome> _result = new ArrayList<MilkIncome>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MilkIncome _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDateMillis;
            _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
            final String _tmpCowName;
            _tmpCowName = _cursor.getString(_cursorIndexOfCowName);
            final double _tmpLiters;
            _tmpLiters = _cursor.getDouble(_cursorIndexOfLiters);
            final double _tmpFatPercent;
            _tmpFatPercent = _cursor.getDouble(_cursorIndexOfFatPercent);
            final double _tmpPricePerLiter;
            _tmpPricePerLiter = _cursor.getDouble(_cursorIndexOfPricePerLiter);
            final String _tmpOwnerUsername;
            if (_cursor.isNull(_cursorIndexOfOwnerUsername)) {
              _tmpOwnerUsername = null;
            } else {
              _tmpOwnerUsername = _cursor.getString(_cursorIndexOfOwnerUsername);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new MilkIncome(_tmpId,_tmpDateMillis,_tmpCowName,_tmpLiters,_tmpFatPercent,_tmpPricePerLiter,_tmpOwnerUsername,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<MilkIncome>> range(final long from, final long to) {
    final String _sql = "SELECT * FROM income WHERE dateMillis BETWEEN ? AND ? ORDER BY dateMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, from);
    _argIndex = 2;
    _statement.bindLong(_argIndex, to);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"income"}, new Callable<List<MilkIncome>>() {
      @Override
      @NonNull
      public List<MilkIncome> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "dateMillis");
          final int _cursorIndexOfCowName = CursorUtil.getColumnIndexOrThrow(_cursor, "cowName");
          final int _cursorIndexOfLiters = CursorUtil.getColumnIndexOrThrow(_cursor, "liters");
          final int _cursorIndexOfFatPercent = CursorUtil.getColumnIndexOrThrow(_cursor, "fatPercent");
          final int _cursorIndexOfPricePerLiter = CursorUtil.getColumnIndexOrThrow(_cursor, "pricePerLiter");
          final int _cursorIndexOfOwnerUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "ownerUsername");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<MilkIncome> _result = new ArrayList<MilkIncome>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MilkIncome _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDateMillis;
            _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
            final String _tmpCowName;
            _tmpCowName = _cursor.getString(_cursorIndexOfCowName);
            final double _tmpLiters;
            _tmpLiters = _cursor.getDouble(_cursorIndexOfLiters);
            final double _tmpFatPercent;
            _tmpFatPercent = _cursor.getDouble(_cursorIndexOfFatPercent);
            final double _tmpPricePerLiter;
            _tmpPricePerLiter = _cursor.getDouble(_cursorIndexOfPricePerLiter);
            final String _tmpOwnerUsername;
            if (_cursor.isNull(_cursorIndexOfOwnerUsername)) {
              _tmpOwnerUsername = null;
            } else {
              _tmpOwnerUsername = _cursor.getString(_cursorIndexOfOwnerUsername);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new MilkIncome(_tmpId,_tmpDateMillis,_tmpCowName,_tmpLiters,_tmpFatPercent,_tmpPricePerLiter,_tmpOwnerUsername,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object totalIncome(final long from, final long to,
      final Continuation<? super Double> $completion) {
    final String _sql = "SELECT COALESCE(SUM(liters * pricePerLiter), 0.0) FROM income WHERE dateMillis BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, from);
    _argIndex = 2;
    _statement.bindLong(_argIndex, to);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Double>() {
      @Override
      @NonNull
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final double _tmp;
            _tmp = _cursor.getDouble(0);
            _result = _tmp;
          } else {
            _result = 0.0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object litersByCow(final String cow, final Continuation<? super Double> $completion) {
    final String _sql = "SELECT COALESCE(SUM(liters), 0.0) FROM income WHERE cowName = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, cow);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Double>() {
      @Override
      @NonNull
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final double _tmp;
            _tmp = _cursor.getDouble(0);
            _result = _tmp;
          } else {
            _result = 0.0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
