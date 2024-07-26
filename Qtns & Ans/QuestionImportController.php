<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Maatwebsite\Excel\Facades\Excel;
use App\Imports\QuestionsImport;

class QuestionImportController extends Controller
{
    /**
     * Show the form for uploading the Excel file.
     *
     * @return \Illuminate\View\View
     */
    public function importForm()
    {
        return view('import-questions'); // Return the view with the form for file upload
    }

    /**
     * Handle the import process.
     *
     * @param \Illuminate\Http\Request $request
     * @return \Illuminate\Http\RedirectResponse
     */
    public function import(Request $request)
    {
        // Validate the uploaded file
        $request->validate([
            'file' => 'required|file|mimes:xlsx,csv' // Allow both Excel and CSV files
        ]);

        // Import the data using the QuestionsImport class
        Excel::import(new QuestionsImport, $request->file('file'));

        // Redirect or return a response
        return redirect()->back()->with('success', 'Questions imported successfully!');
    }
}

