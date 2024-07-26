<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Challenge extends Model
{
    protected $primaryKey = 'challengeID';
    public $incrementing = false; // Because challengeID is a string and not an auto-incrementing integer
    protected $keyType = 'string';

    protected $fillable = [
        'challengeID',
        'startDate',
        'endDate',
        'noOfQuestions',
        'duration',
        'noOfAttempts',
    ];
}
