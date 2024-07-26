@extends('layouts.app1')

@section('title', 'School Details')

@section('content')
    <h1>School Details</h1>

    <p><strong>Registration Number:</strong> {{ $school->registrationNumber }}</p>
    <p><strong>Name:</strong> {{ $school->name }}</p>
    <p><strong>District:</strong> {{ $school->district }}</p>
    <p><strong>Representative Name:</strong> {{ $school->representativeName }}</p>
    <p><strong>Representative Email:</strong> {{ $school->representativeEmail }}</p>

    <a href="{{ route('schools.index') }}" class="btn btn-secondary">Back to List</a>
@endsection
