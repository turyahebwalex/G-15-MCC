<?php

namespace App\Http\Controllers;

use App\Models\School;
use Illuminate\Http\Request;

class SchoolController extends Controller
{
    // Display a listing of the schools
    public function index()
    {
        $schools = School::all();
        return view('schools.index', compact('schools'));
    }

    // Show the form for creating a new school
    public function create()
    {
        return view('schools.create');
    }

    // Store a newly created school in storage
    public function store(Request $request)
    {
        $request->validate([
            'registrationNumber' => 'required|string|max:255|unique:schools,registrationNumber',
            'name' => 'required|string|max:255',
            'district' => 'required|string|max:255',
            'representativeName' => 'required|string|max:255',
            'representativeEmail' => 'required|email|max:255',
        ]);

        School::create($request->all());

        return redirect()->route('schools.index')->with('success', 'School created successfully.');
    }

    // Display the specified school
    public function show($registrationNumber)
    {
        $school = School::findOrFail($registrationNumber);
        return view('schools.show', compact('school'));
    }

    // Show the form for editing the specified school
    public function edit($registrationNumber)
    {
        $school = School::findOrFail($registrationNumber);
        return view('schools.edit', compact('school'));
    }

    // Update the specified school in storage
    public function update(Request $request, $registrationNumber)
    {
        $request->validate([
            'name' => 'required|string|max:255',
            'district' => 'required|string|max:255',
            'representativeName' => 'required|string|max:255',
            'representativeEmail' => 'required|email|max:255',
        ]);

        $school = School::findOrFail($registrationNumber);
        $school->update($request->all());

        return redirect()->route('schools.index')->with('success', 'School updated successfully.');
    }

    // Remove the specified school from storage
    public function destroy($registrationNumber)
    {
        $school = School::findOrFail($registrationNumber);
        $school->delete();

        return redirect()->route('schools.index')->with('success', 'School deleted successfully.');
    }
}
