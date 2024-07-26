@extends('layouts.app1')

@section('title', 'Add New School')

@section('content')
    <h1>Add New School</h1>

    <form action="{{ route('schools.store') }}" method="POST">
        @csrf

        <div class="form-group">
            <label for="registrationNumber">Registration Number</label>
            <input type="text" id="registrationNumber" name="registrationNumber" class="form-control" value="{{ old('registrationNumber') }}" required>
            @error('registrationNumber')
                <div class="text-danger">{{ $message }}</div>
            @enderror
        </div>

        <div class="form-group">
            <label for="name">Name</label>
            <input type="text" id="name" name="name" class="form-control" value="{{ old('name') }}" required>
            @error('name')
                <div class="text-danger">{{ $message }}</div>
            @enderror
        </div>

        <div class="form-group">
            <label for="district">District</label>
            <input type="text" id="district" name="district" class="form-control" value="{{ old('district') }}" required>
            @error('district')
                <div class="text-danger">{{ $message }}</div>
            @enderror
        </div>

        <div class="form-group">
            <label for="representativeName">Representative Name</label>
            <input type="text" id="representativeName" name="representativeName" class="form-control" value="{{ old('representativeName') }}" required>
            @error('representativeName')
                <div class="text-danger">{{ $message }}</div>
            @enderror
        </div>

        <div class="form-group">
            <label for="representativeEmail">Representative Email</label>
            <input type="email" id="representativeEmail" name="representativeEmail" class="form-control" value="{{ old('representativeEmail') }}" required>
            @error('representativeEmail')
                <div class="text-danger">{{ $message }}</div>
            @enderror
        </div>

        <button type="submit" class="btn btn-primary">Add School</button>
    </form>
@endsection
