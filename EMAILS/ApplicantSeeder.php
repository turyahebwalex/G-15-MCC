<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\Applicant;

class ApplicantSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run()
    {
        Applicant::create([
            'email' => 'test1@example.com',
            'status' => 'pending',
        ]);

        Applicant::create([
            'email' => 'test2@example.com',
            'status' => 'pending',
        ]);

        Applicant::create([
            'email' => 'test3@example.com',
            'status' => 'pending',
        ]);
    }
    
}
