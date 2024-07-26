<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class School extends Model
{
    protected $table ='school';
    protected $primaryKey = 'registrationNumber';
    public $incrementing = false; // Because registrationNumber is a string and not an auto-incrementing integer
    protected $keyType = 'string';

    protected $fillable = [
        'registrationNumber',
        'name',
        'district',
        'representativeName',
        'representativeEmail',
    ];
    public function pupils()
    {
        return $this->hasMany(Pupil::class,'registrationNumber');
    }
}
